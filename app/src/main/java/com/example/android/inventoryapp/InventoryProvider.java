package com.example.android.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.android.inventoryapp.InventoryContract.*;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.*;

/**
 * Created by phartmann on 09/03/2018.
 */

public class InventoryProvider extends ContentProvider {

    /* Inicialize Helper */
    private InventoryDbHelper dbHelper;

    /* Constants for UriMatcher */
    private static final int INVENTORY = 10;
    private static final int INVENTORY_ID = 99;

    /* Init UriMatcher */
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(CONTENT_AUTORITY, PATH_INVENTORY, INVENTORY);
        URI_MATCHER.addURI(CONTENT_AUTORITY, PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    /* Log anything */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName( );

    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query( @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder ) {

        /* Get the Db */
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        /* Get Cursor to hold result */
        Cursor cursor;

        /* Match URI first */
        int match = URI_MATCHER.match(uri);

        switch (match){
            case INVENTORY:
                /* Query DB directly */
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                /* Configure WHERE argument to query */
                selection = _ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                /* Do the query given arguments */
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unkown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType( @NonNull Uri uri ) {
        final int match = URI_MATCHER.match(uri);
        switch (match){
            case INVENTORY:
                return CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match" + match);
        }
    }

    @Nullable
    @Override
    public Uri insert( @NonNull Uri uri, @Nullable ContentValues values ) {
        final int match = URI_MATCHER.match(uri);
        switch (match){
            case INVENTORY:
                return insertInvertory(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not support for " + uri);
        }
    }

    private Uri insertInvertory( Uri uri, ContentValues values ) {

        /* Check if Item isn't null */
        String item = values.getAsString(ITEM_COLUMN);
        if (item == null){
            throw new IllegalArgumentException("Item must have a name");
        }

        /* Check if priceEdit is valid */
        double price = values.getAsDouble(PRICE_COLUMN);
        if (price < 0){
            throw new IllegalArgumentException("Price must be valid");
        }

        /* Check if quantityEdit is valid */
        int quantity = values.getAsInteger(QUANTITY_COLUMN);
        if ( quantity < 0 ){
            throw new IllegalArgumentException("Quantity must be valid");
        }

        /* Check if contactEdit is valid */
        Integer contact = values.getAsInteger(CONTACT_COLUMN);
        if (contact != null){
            if (contact < 0 ){
                throw new IllegalArgumentException("Contact must be valid. Only numbers are accepted");
            }
        }

        /* Get Database  and Insert data on DB */
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long insertId = db.insert(TABLE_NAME, null, values);

        /* if insertion fail, log it! */
        if (insertId == -1) {
            Log.e(LOG_TAG, "Failed to insert data for " + uri );
            return null;
        }

        /* Notify changes and return */
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, insertId);
    }

    @Override
    public int delete( @NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs ) {
        /* Get Database */
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        /* Match the uri */
        int match = URI_MATCHER.match(uri);
       /* Track the rows removed */
        int rowsRemoved;

        switch (match){
            case INVENTORY:
                rowsRemoved = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = _ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsRemoved = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for the following uri: " + uri);
        }

        if (rowsRemoved != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsRemoved;
    }

    @Override
    public int update( @NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs ) {

        /* Handle Uri */
        final int match = URI_MATCHER.match(uri);

        switch (match){
            case INVENTORY:
                return updateInventory(uri, values, selection, selectionArgs);
            case INVENTORY_ID:
                selection = _ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Uptade is not supported for the following uri: " + uri);
        }
    }

    private int updateInventory( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {

        int rowsUpdated;

        /* Check if item name is valid */
        if (values.containsKey(ITEM_COLUMN)){
            String item = values.getAsString(ITEM_COLUMN);
            if (item == null){
                throw new IllegalArgumentException("Product must have a name");
            }
        }

        /* Check if priceEdit is valid */
        if (values.containsKey(PRICE_COLUMN)){
            if (values.getAsDouble(PRICE_COLUMN) < 0) {
                throw new IllegalArgumentException("Price must be higher then or at least 0");
            }
        }

        /* Check if quantityEdit is valid */
        if (values.containsKey(QUANTITY_COLUMN) && values.getAsInteger(QUANTITY_COLUMN) < 0) {
            throw new IllegalArgumentException("Quantity must be higher then or at least 0");
        }

        /* Return if there are any value */
        if (values.size() == 0){
            rowsUpdated = 0;
            return rowsUpdated;
        }

        /* Get DB */
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /* Rows Updated */
        rowsUpdated = db.update(TABLE_NAME, values, selection, selectionArgs);

        /* Notify Changes */
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

}