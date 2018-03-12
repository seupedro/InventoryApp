package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.CONTENT_URI;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.ITEM_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.PRICE_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.QUANTITY_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry._ID;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /* Tag to search on logs */
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /* Loader ID */
    private static final int EDITOR_LOADER_ID = 0;

    /* Current Item */
    private static Uri currentItem = null;

    /* Global EditText fields */
    private EditText itemEdit;
    private EditText priceEdit;
    private EditText quantityEdit;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        /* Find on layout */
        itemEdit = findViewById(R.id.item_editview);
        priceEdit = findViewById(R.id.price_editview);
        quantityEdit = findViewById(R.id.quantity_editview);

         /* Get URI from intent */
        currentItem = getIntent().getData();

        /* Set correct label */
        if (currentItem != null){
            setTitle("Edit Item");
            getLoaderManager().initLoader(EDITOR_LOADER_ID, null, this);
        } else {
            setTitle("New Item");

        }

    }



    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
        super.onPrepareOptionsMenu(menu);
        /* If isn't a new pet, hide delete option */
        if (currentItem == null){
            menu.findItem(R.id.delete_buton_editor).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        //Inflate the menu
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        int id = item.getItemId();

        switch (id) {
            case R.id.delete_buton_editor:
                deleteItem();
                return true;
            case R.id.save_button_editor:
                saveItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteItem() {
        /* Check if user really wants do it */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want delete this item? It cannot be undone.");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                /* Delete Item */
                getContentResolver().delete(currentItem, null, null);
                /* Tell to the user */
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.create().show();
    }

    private void saveItem() {

        /* Get values from fields */
        ContentValues values = new ContentValues();
        values.put(ITEM_COLUMN, itemEdit.getText().toString().trim());
        values.put(PRICE_COLUMN, priceEdit.getText().toString().trim());
        values.put(QUANTITY_COLUMN, quantityEdit.getText().toString().trim());

        Uri newUri = getContentResolver().insert(CONTENT_URI, values);
        getContentResolver().notifyChange(CONTENT_URI, null);

        Toast.makeText(this, String.valueOf(newUri), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
        String[] projection = {
                _ID,
                ITEM_COLUMN,
                PRICE_COLUMN,
                QUANTITY_COLUMN
        };

        return new CursorLoader(this,
                currentItem,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished( Loader <Cursor> loader, Cursor cursor ) {
        /* Check if cursor is valid or not */
        if (cursor == null || cursor.getCount() < 1){
            return;
        }
        /* Fill fields from DB */
        if (cursor.moveToNext()){
            itemEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_COLUMN)));
            priceEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(PRICE_COLUMN)));
            quantityEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY_COLUMN)));
        }
    }

    @Override
    public void onLoaderReset( Loader <Cursor> loader ) {
        itemEdit.setText("");
        priceEdit.setText("");
        quantityEdit.setText("");
    }
}
