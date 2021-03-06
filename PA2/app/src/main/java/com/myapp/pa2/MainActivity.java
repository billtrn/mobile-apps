package com.myapp.pa2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myapp.pa2.model.Book;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BookAdapter.OnItemClickedListener {

    // Conss for Other Activitites
    int LAUNCH_BOOK_ACTIVITY = 1;
    int LAUNCH_NEW_BOOK_ACTIVITY = 2;

    RecyclerView recyclerView;
    private ArrayList<Book> bookList;
    BookSQLiteHelper dbHelper;
    BookAdapter bookAdapter;

    /**
     *  Loading books from Database & put into rows on UI
     */
    private void loadData() {
        bookList = dbHelper.getAllBooks();
        bookAdapter = new BookAdapter(bookList, this);
        recyclerView.setAdapter(bookAdapter);
    }

    /**
     * onCreate screen
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init DB instance
        dbHelper = BookSQLiteHelper.getInstance(this);
        recyclerView = findViewById(R.id.book_list);
        loadData();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        addDividers();

        // Floating Button from Adding new book
        FloatingActionButton addBookFloatingButton = findViewById(R.id.fab);
        addBookFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddBookClick();
            }
        });
    }

    /**
     *  Adding gray colored dividers between rows of book
     *
     */
    private void addDividers() {
        // Divider in portrait mode
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );

        // Divider in landscape mode
        Drawable horizontalDivider = ContextCompat.getDrawable(
                MainActivity.this,
                R.drawable.horizontal_divider
        );
        horizontalDecoration.setDrawable(horizontalDivider);

        // Add divider to Recycler view
        recyclerView.addItemDecoration(horizontalDecoration);
    }

    /**
     * Handler function for clicking a book item action
     * @param position
     */
    @Override
    public void onBookClick(int position) {
        // Launch BookActivity when a book is clicked
        Intent intent = new Intent(this, BookActivity.class);
        intent.putExtra("bookID", bookList.get(position).getId());
        startActivityForResult(intent, LAUNCH_BOOK_ACTIVITY);
    }

    public void onAddBookClick() {
        Intent intent = new Intent(this, NewBookActivity.class);
        startActivityForResult(intent, LAUNCH_NEW_BOOK_ACTIVITY);
    }

    /**
     *  Refresh the Book List on return from BookActivity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_BOOK_ACTIVITY ||
            requestCode == LAUNCH_NEW_BOOK_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                loadData();
            }
        }
    }

    @Override
    protected void onDestroy() {
        // Close database connection when the app is destroyed
        dbHelper.close();
        super.onDestroy();
    }
}
