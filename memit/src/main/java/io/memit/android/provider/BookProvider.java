package io.memit.android.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import io.memit.android.BuildConfig;

/**
 * Created by peter on 1/29/17.
 */

public class BookProvider extends ContentProvider{

    private static final String TAG = BookProvider.class.getSimpleName();

    private DatabaseOpenHelper dbHelper;

    private static final int CODE_ALL_BOOK= 100;
    private static final int CODE_BOOK_ID = 101;

    private static final SparseArray<String> URI_CODE_TABLE_MAP =  new SparseArray<>();


    private static final UriMatcher URI_MATCHER =  new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_CODE_TABLE_MAP.put(CODE_ALL_BOOK, DatabaseOpenHelper.Tables.BOOK);
        URI_CODE_TABLE_MAP.put(CODE_BOOK_ID,  DatabaseOpenHelper.Tables.BOOK);

        URI_MATCHER.addURI(BookContract.AUTHORITY,
                BookContract.Book.PATH,
                CODE_ALL_BOOK);

        URI_MATCHER.addURI(BookContract.AUTHORITY,
                BookContract.Book.PATH + "/#",
                CODE_BOOK_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = DatabaseOpenHelper.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public  Cursor query(@NonNull Uri uri,
                                String[] projection,
                                String selection,
                                String[] selectionArgs,
                                String sortOrder) throws IllegalArgumentException {
        if(BuildConfig.DEBUG)
            Log.d(TAG, "Uri: " + uri);

        Cursor cursor;
        if (projection == null) {
            throw new IllegalArgumentException("Projection can't be null");
        }
        sortOrder = (sortOrder == null ? BaseColumns._ID : sortOrder);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        final int code = URI_MATCHER.match(uri);
        switch (code) {
            case CODE_ALL_BOOK:
                cursor = database.query(URI_CODE_TABLE_MAP.get(code),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id;
        final int code = URI_MATCHER.match(uri);
        switch (code) {
            case CODE_ALL_BOOK:
                id = dbHelper
                        .getWritableDatabase()
                        .insertOrThrow(URI_CODE_TABLE_MAP.get(code), null, values);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }

        notifyUris(uri);
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowCount;
        final int code = URI_MATCHER.match(uri);
        switch (code) {
            /*
            case CODE_ALL_DEVICES:
            case CODE_ALL_MANUFACTURERS:
                rowCount = helper
                        .getWritableDatabase()
                        .delete(URI_CODE_TABLE_MAP.get(code),
                                selection,
                                selectionArgs);
                break;
            */
            case CODE_BOOK_ID:
                if (selection == null && selectionArgs == null) {
                    return BookDatabaseOperations
                            .getInstance(dbHelper)
                            .removeBook(uri.getLastPathSegment());
                } else {
                    throw new IllegalArgumentException("Selection must be " +
                            "null when specifying ID as part of uri.");
                }
            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }
    }


     public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private void notifyUris(Uri affectedUri) {
        final ContentResolver contentResolver = getContext().getContentResolver();
        if (contentResolver != null) {
            contentResolver.notifyChange(affectedUri, null);
//            contentResolver.notifyChange(DevicesContract.DeviceManufacturer.CONTENT_URI, null);
        }
    }
}
