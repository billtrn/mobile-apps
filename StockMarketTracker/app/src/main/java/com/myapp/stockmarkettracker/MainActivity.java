/**
 *
 * BUILT BY JULIAN (PHONG) NGUYEN & BILL TRAN
 *
 */
package com.myapp.stockmarkettracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;

import com.myapp.stockmarkettracker.databinding.ActivityMainBinding;
import com.myapp.stockmarkettracker.ui.StockAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements StockAdapter.OnItemClickedListener {

    private ActivityMainBinding binding;

    RecyclerView recyclerView;
    private ArrayList<Stock> stockList;
    StockSQLiteHelper dbHelper;
    StockAdapter stockAdapter;

    // Codes for Other Activities
    int LAUNCH_STOCK_ACTIVITY = 1;
    int LAUNCH_NEW_STOCK_ACTIVITY = 2;

    /**
     *  Loading stocks from Database & put into rows on UI
     */
    private void loadData() {
        // Query all stocks from DB
        stockList = dbHelper.getAllStocks();

        // Frame for Stock list
        stockAdapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(stockAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Option list for Adding new Stock
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Init DB instance
        dbHelper = StockSQLiteHelper.getInstance(this);

        // Filling the StockList with Stock data persisted in DB
        recyclerView = findViewById(R.id.stock_list);
        loadData();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        addDividers();
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
     * Handler function for clicking a Stock item action
     * @param position
     */
    @Override
    public void onStockClick(int position) {
        // Launch StockActivity when a stock is clicked
        Intent intent = new Intent(this, StockDetailsActivity.class);
        intent.putExtra("symbol", stockList.get(position).getSymbol());
        startActivityForResult(intent, LAUNCH_STOCK_ACTIVITY);
    }

    public void onAddStockClick() {
        Intent intent = new Intent(this, NewStockActivity.class);
        startActivityForResult(intent, LAUNCH_NEW_STOCK_ACTIVITY);
    }

    /**
     *  Refresh the Stock List on return from StockActivity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_STOCK_ACTIVITY ||
                requestCode == LAUNCH_NEW_STOCK_ACTIVITY) {
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

    /**
     * Handle OptionBar Drop down menu list
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_stock_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle action when "Add Stock" is clicked
        if (id == R.id.add_stock_option) {
            onAddStockClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
