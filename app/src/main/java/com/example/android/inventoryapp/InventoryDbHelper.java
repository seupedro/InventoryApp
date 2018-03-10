package com.example.android.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.InventoryContract.InventoryEntry;

import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.*;

/**
 * Created by phartmann on 09/03/2018.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 0;
    public static final String DB_NAME = "inventory.db";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ITEM_COLUMN + " TEXT NOT NULL, " +
                    PRICE_COLUMN + " INTEGER NOT NULL DEFAULT 0, " +
                    QUANTITY_COLUMN + " INTEGER NOT NULL DEFAULT 0 " + ")";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public InventoryDbHelper( Context context ) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
