package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.CONTACT_COLUMN;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.CONTENT_URI;
import static com.example.android.inventoryapp.InventoryContract.InventoryEntry.IMAGE_COLUMN;
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

    /* Intent Request Codes */
    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    public static Uri imageUri = null;

    /* Global EditText fields */
    private EditText itemEdit;
    private EditText priceEdit;
    private EditText quantityEdit;
    private EditText contactEdit;
    private ImageButton contactButton;
    private ImageView imageItem;

    /* Track if changes was made on activity */
    private boolean isChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener( ) {
        @Override
        public boolean onTouch( View v, MotionEvent event ) {
            isChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        /* Find on layout */
        itemEdit = findViewById(R.id.item_editview);
        priceEdit = findViewById(R.id.price_editview);
        quantityEdit = findViewById(R.id.quantity_editview);
        contactEdit = findViewById(R.id.contact_editview);
        contactButton = findViewById(R.id.contact_button);
        imageItem = findViewById(R.id.picture_item);

        /* Set Change Listeners on fields */
        itemEdit.setOnTouchListener(touchListener);
        priceEdit.setOnTouchListener(touchListener);
        quantityEdit.setOnTouchListener(touchListener);
        contactEdit.setOnTouchListener(touchListener);
        imageItem.setOnTouchListener(touchListener);

         /* Get URI from intent */
        currentItem = getIntent().getData();

        /* Do a Call on Button */
        contactButton.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick( View v ) {
                callProvider();
            }
        });

        /* Set correct label */
        if (currentItem != null){
            setTitle("Edit Item");
            getLoaderManager().initLoader(EDITOR_LOADER_ID, null, this);
        } else {
            setTitle("New Item");
        }
        /* Get Image to Item */
        getItemImage();
    }

    private void getItemImage() {

        final String dialogChoices[] = {"From camera", "From gallery"};
        /* Picture */
        imageItem.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick( View v ) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorActivity.this);
                alertDialog.setTitle("Add photo ");
                alertDialog.setItems(dialogChoices, new DialogInterface.OnClickListener( ) {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        switch (which) {
                            case 1:
                                /* Gallery option */
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                                galleryIntent.setType("image/*");
                                startActivityForResult(galleryIntent, GALLERY_REQUEST);
                                break;
                            case 0:
                                /* Camera Option */
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
                alertDialog.create().show();
            }
        });
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            /* Get a photo from gallery */
            case GALLERY_REQUEST:
                /* Check if result is correct */
                if (resultCode == RESULT_OK){
                    try {
                        imageUri = data.getData();
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageItem.setImageBitmap(bitmap);
                        imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace( );
                    }
                } else {
                    /* In case of error, give some feedback to user */
                    Toast.makeText(this, "Houve um problema ao selecionar a foto", Toast.LENGTH_SHORT).show();
                }
                break;
            /* Get a photo from gallery */
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK){
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imageItem.setImageBitmap(bitmap);

                    /* Attempts */
                    // firstTry(data);
                    // secondTry(data);

                } else {
                    Toast.makeText(this, "Houve um problema ao selecionar a foto", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void firstTry(Intent data) {
        imageUri = data.getData();
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageItem.setImageBitmap(bitmap);
            imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } catch (FileNotFoundException e) {
            e.printStackTrace( );
        }
    }

    private void secondTry(Intent data){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            imageItem.setImageBitmap(bitmap);
            imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }

    private void callProvider() {
        String providerNumber = contactEdit.getText().toString().trim();
        if (providerNumber != null && !providerNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + providerNumber));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Número do fornecedor não pode estar em branco", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        if (!isChanged){
            super.onBackPressed();
            return;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Changes was made. Do you want to discard it?");
            builder.setPositiveButton("DISCARD", new DialogInterface.OnClickListener( ) {
                @Override
                public void onClick( DialogInterface dialog, int which ) {
                    finish();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener( ) {
                @Override
                public void onClick( DialogInterface dialog, int which ) {
                    if (dialog != null){
                        dialog.dismiss();
                    }
                }
            });
            builder.create().show();
        }
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

        if (checkFields()){
            /* Get values from fields */
            ContentValues values = new ContentValues();
            values.put(ITEM_COLUMN, itemEdit.getText().toString().trim());
            values.put(PRICE_COLUMN, priceEdit.getText().toString().trim());
            values.put(QUANTITY_COLUMN, quantityEdit.getText().toString().trim());
            values.put(CONTACT_COLUMN, contactEdit.getText().toString().trim());
            values.put(IMAGE_COLUMN, String.valueOf(imageUri));

        /* If is a new Item, insert on db. Otherwise update current item */
            if (currentItem == null){
                Uri newUri = getContentResolver().insert(CONTENT_URI, values);
                Toast.makeText(this, String.valueOf(newUri), Toast.LENGTH_SHORT).show();
            } else {
                int updatedRow = getContentResolver().update(currentItem, values, null, null);
                Snackbar.make(findViewById(R.id.rootView), "Rows updated: " + updatedRow, Snackbar.LENGTH_SHORT).show();
            }
        /* Notify changes */
            getContentResolver().notifyChange(CONTENT_URI, null);
        /* Close activity */
            finish();
        }
    }

    private boolean checkFields() {

        /* Get values from fields */
        String itemValue =  itemEdit.getText().toString().trim();
        String priceValue = priceEdit.getText().toString().trim();
        String quantityValue = quantityEdit.getText().toString().trim();
        String contactValue = contactEdit.getText().toString().trim();

        /* Match a regex on contact number */
        Pattern regex = Pattern.compile("(^\\d{2})?\\d{8,9}");
        Matcher matcher = regex.matcher(contactValue);
        Boolean numberIsValid = matcher.matches();

        /* Check if name is valid */
        if (itemValue.isEmpty() || itemEdit == null){
            Toast.makeText(this, "O produto precisa ter um nome", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        /* Check if price is valid */
        if (!priceValue.isEmpty()){
            if (Double.parseDouble(priceValue) < 0 || priceEdit == null){
                Toast.makeText(this, "Preço precisa ser maior que 0, ou pelo menos igual a 0", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        } else {
            Toast.makeText(this, "Preço não pode estar em branco", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        /* Check if quantity is valid */
        if (!quantityValue.isEmpty()) {
            if (Integer.parseInt(quantityValue) < 0 || quantityEdit == null) {
                Toast.makeText(this, "Quantidade precisa ser maior que 0, ou pelo menos igual a 0", Toast.LENGTH_SHORT)
                        .show( );
                return false;
            }
        } else {
            Toast.makeText(this, "Quantidade não pode estar em branco", Toast.LENGTH_SHORT)
                    .show( );
            return false;
        }

        /* If contact number is not empty, check it's valid */
        if (!contactValue.isEmpty()){
            if (!numberIsValid){
                Toast.makeText(this, "O número de contato  não é válido", Toast.LENGTH_SHORT)
                        .show();
                contactEdit.setHint("Insira apenas números");
                return false;
            }
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
        String[] projection = {
                _ID,
                ITEM_COLUMN,
                PRICE_COLUMN,
                QUANTITY_COLUMN,
                CONTACT_COLUMN,
                IMAGE_COLUMN
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
            contactEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN)));
            /* Check if there is an image */
            String imageStringDb = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_COLUMN));
            if (imageStringDb != null && !TextUtils.isEmpty(imageStringDb)){
                /* Fill image from gallery */
                imageUri = Uri.parse(imageStringDb);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageItem.setImageBitmap(bitmap);
                    imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } catch (FileNotFoundException e) {
                    e.printStackTrace( );
                }
            }
        }
    }

    @Override
    public void onLoaderReset( Loader <Cursor> loader ) {
        itemEdit.setText("");
        priceEdit.setText("");
        quantityEdit.setText("");
        contactEdit.setText("");
        imageUri = null;
    }

}
