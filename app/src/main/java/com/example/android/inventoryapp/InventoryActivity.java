package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.CONTENT_URI;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.ITEM_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.PRICE_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.QUANTITY_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry._ID;

public class InventoryActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = InventoryActivity.class.getSimpleName();
    /* Loader Constant ID */
    private static final int INVENTORY_LOADER_ID = 0;
    /* Loop control */
    private int i = 0;

    /* Adapter to the listview */
    InventoryCursorAdapter inventoryCursorAdapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Find views on layout */
        final ListView inventoryList = findViewById(R.id.list);

        /* Set Adapter */
        inventoryCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryList.setAdapter(inventoryCursorAdapter);

        /* Set up a listener to edit an Item */
        inventoryList.setOnItemClickListener(new AdapterView.OnItemClickListener( ) {
            @Override
            public void onItemClick( AdapterView <?> parent, View view, int position, long id ) {
                Uri currentItemUri = ContentUris.withAppendedId(CONTENT_URI, id);
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });

        /* Fab to make a intent to the editor/update activity */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick( View v ) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        /* Start Loader */
        getLoaderManager().initLoader(INVENTORY_LOADER_ID, null, this);
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
        switch (id) {
            case R.id.insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.delete_all:
                deleteAllData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyData() {
        /* Arrays to put random data on database */
        String[] items = {"Macbook Pro Retina", "iMac", "Google Pixel XL 2", "Xiaomi Mi A1"};
        Double[] price = { 1200.0, 1500.0, 500.0, 300.0};
        int[] quantity = { 50, 55, 215, 10};

        /* Loop must be infinity */
        if (i == items.length){
            i = 0;
        }

        /* Insert a random sample on Database */
        ContentValues values = new ContentValues();
        values.put(ITEM_COLUMN, items[i]);
        values.put(PRICE_COLUMN,  price[i]);
        values.put(QUANTITY_COLUMN, quantity[i]);
        i++;

        getContentResolver().insert(CONTENT_URI, values);

        /* Give a feedback to the user */
        Snackbar.make(findViewById(R.id.coordinator), "New sample added", Snackbar.LENGTH_SHORT).show();
    }

    private void deleteAllData() {
        /* Check if user really wants do it */
        AlertDialog.Builder alertbuilder = new AlertDialog.Builder(this);
        alertbuilder.setMessage("Do you have sure? Any data will not be recovered.");
        alertbuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                /* Remove all Data */
                int dataRemoved = getContentResolver().delete(CONTENT_URI, null, null);
                /* Give some feedback to the user */
                if (dataRemoved > 0){
                    Snackbar.make(findViewById(R.id.coordinator), "All data was removed", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(R.id.coordinator), "Nothing to remove", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        alertbuilder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                if (dialog != null) {
                    dialog.dismiss( );
                }
            }
        });

        /* Create alert and show */
        alertbuilder.create().show();
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

    public static void sellQuatity( int quantity ) {

    }
}
