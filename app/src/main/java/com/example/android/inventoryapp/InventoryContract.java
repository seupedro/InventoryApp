package com.example.android.inventoryapp;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

/**
 * Created by phartmann on 09/03/2018.
 */

public final class InventoryContract {

    public InventoryContract(){}

    /* Provider Constants */
    public static final String CONTENT_AUTORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTORITY);
    public static final String PATH_INVENTORY = "inventory";
    public static final String PATH_INVENTORY_ID = "inventory/#";

    public static class InventoryEntry implements BaseColumns{

        /* MIME types */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTORITY + "/" + PATH_INVENTORY_ID;

        /* Table Constant */
        public static final String TABLE_NAME = "inventory";

        /* Colunms Constants */
        public static final String _ID = BaseColumns._ID;
        public static final String ITEM_COLUMN = "item";
        public static final String PRICE_COLUMN = "priceEdit";
        public static final String QUANTITY_COLUMN = "quantityEdit";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
    }
}
