package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.ITEM_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.PRICE_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.QUANTITY_COLUMN;


/**
 * Created by phartmann on 10/03/2018.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter( Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView( Context context, Cursor cursor, ViewGroup parent ) {
        /* Create new views from cursor to adapter */
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView( View view, Context context, Cursor cursor ) {

        /* Find views on Layout */
        TextView item_tv = view.findViewById(R.id.item_tv);
        TextView price_tv = view.findViewById(R.id.price_tv);
        TextView quantity_tv = view.findViewById(R.id.quatity_tv);

        /* Fill data from cursor on each textview */
        item_tv.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(ITEM_COLUMN))
        );

        price_tv.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(PRICE_COLUMN))
        );

        quantity_tv.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY_COLUMN))
        );
    }
}
