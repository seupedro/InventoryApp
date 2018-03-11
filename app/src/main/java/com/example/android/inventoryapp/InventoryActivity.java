package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.CONTENT_URI;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.ITEM_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.PRICE_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.QUANTITY_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry._ID;

public class InventoryActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    InventoryCursorAdapter inventoryCursorAdapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Find views on layout */
        ListView inventoryList = findViewById(R.id.list);

        /* Set Adapter */
        inventoryCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryList.setAdapter(inventoryCursorAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick( View view ) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        /* Start Loader */
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater( ).inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId( );

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*  find data on DB */
    @Override
    public Loader<Cursor> onCreateLoader( int id, Bundle args ) {

        String[] projection = {
                _ID,
                ITEM_COLUMN,
                PRICE_COLUMN,
                QUANTITY_COLUMN
        };

        return new CursorLoader(this,
                CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    /* Fill data from cursor to list */
    @Override
    public void onLoadFinished( Loader <Cursor> loader, Cursor data ) {
        inventoryCursorAdapter.swapCursor(data);
    }

    /* When data is updated? */
    @Override
    public void onLoaderReset( Loader <Cursor> loader ) {
        inventoryCursorAdapter.swapCursor(null);
    }
}
