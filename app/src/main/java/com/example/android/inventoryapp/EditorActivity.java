package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.CONTENT_URI;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.ITEM_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.PRICE_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.QUANTITY_COLUMN;

public class EditorActivity extends AppCompatActivity {

    /* Global EditText fields */
    EditText itemEdit;
    EditText priceEdit;
    EditText quantityEdit;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        /* Find on layout */
        itemEdit = findViewById(R.id.item_editview);
        priceEdit = findViewById(R.id.price_editview);
        quantityEdit = findViewById(R.id.quantity_editview);
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
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.save_button_editor:
                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show( );
                saveItem();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void saveItem() {

        /* Get values from fields */
        ContentValues values = new ContentValues();
        values.put(ITEM_COLUMN, itemEdit.getText().toString().trim());
        values.put(PRICE_COLUMN, priceEdit.getText().toString().trim());
        values.put(QUANTITY_COLUMN, quantityEdit.getText().toString().trim());

        Uri newUri = getContentResolver().insert(CONTENT_URI, values);
        Toast.makeText(this, String.valueOf(newUri), Toast.LENGTH_SHORT).show();
    }
}
