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

public class MemitProvider extends ContentProvider{

    private static final String TAG = MemitProvider.class.getSimpleName();

    private DatabaseOpenHelper dbHelper;

    private static final byte CODE_ALL_BOOKS = 100;
    private static final byte CODE_BOOK_ID = 101;
    private static final byte CODE_ALL_LECTURES= 103;
    private static final byte CODE_LECTURE_ID = 102;

    private static final SparseArray<String> URI_CODE_TABLE_MAP =  new SparseArray<>();


    private static final UriMatcher URI_MATCHER =  new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_CODE_TABLE_MAP.put(CODE_ALL_BOOKS, DatabaseOpenHelper.Tables.BOOK);
        URI_CODE_TABLE_MAP.put(CODE_BOOK_ID,  DatabaseOpenHelper.Tables.BOOK);
        URI_CODE_TABLE_MAP.put(CODE_LECTURE_ID, DatabaseOpenHelper.Tables.LECTURE);
        URI_CODE_TABLE_MAP.put(CODE_ALL_LECTURES, DatabaseOpenHelper.Tables.LECTURE);


        URI_MATCHER.addURI(Contract.AUTHORITY, Contract.Book.PATH, CODE_ALL_BOOKS);
        URI_MATCHER.addURI(Contract.AUTHORITY, Contract.Book.PATH + "/#", CODE_BOOK_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY, Contract.Lecture.PATH + "/#" , CODE_LECTURE_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY, Contract.Lecture.PATH, CODE_ALL_LECTURES);
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
        sortOrder = (sortOrder == null ? BaseColumns._ID : sortOrder);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        final int code = URI_MATCHER.match(uri);
        Log.d(TAG, "code: " + code);
        switch (code) {
            case CODE_ALL_BOOKS:
                cursor = BookDatabaseOperations
                        .getInstance(getContext())
                        .getAllBooks();
                break;
            case CODE_LECTURE_ID:
                String bookId = uri.getLastPathSegment();
                cursor = LectureDatabaseOperation
                        .getInstance(getContext())
                        .getAllLectures(Long.valueOf(bookId));
                break;
            case CODE_BOOK_ID:
                if (selection == null) {
                    selection = BaseColumns._ID
                            + " = "
                            + uri.getLastPathSegment();
                } else {
                    throw new IllegalArgumentException("Selection must " +
                            "be null when specifying ID as part of uri.");
                }
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
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "[getType] uri: " + uri);
        final int code = URI_MATCHER.match(uri);
        switch (code) {
            case CODE_ALL_BOOKS:
                return String.format("%s/vnd.%s.%s",
                        ContentResolver.CURSOR_DIR_BASE_TYPE,
                        Contract.AUTHORITY,
                        Contract.Book.PATH);
            case CODE_BOOK_ID:
                return String.format("%s/vnd.%s.%s",
                        ContentResolver.CURSOR_ITEM_BASE_TYPE,
                        Contract.AUTHORITY,
                        Contract.Book.PATH);
            default:
                return null;
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id;
        Log.d(TAG, "Creating [uri="+uri+"] values: " + values);
        final int code = URI_MATCHER.match(uri);
        switch (code) {
            case CODE_ALL_BOOKS:
                id = dbHelper
                        .getWritableDatabase()
                        .insertOrThrow(URI_CODE_TABLE_MAP.get(code), null, values);
                break;
            case CODE_ALL_LECTURES:
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
        Uri rootUri;
        switch (code) {
            case CODE_BOOK_ID:
                    rowCount = BookDatabaseOperations
                            .getInstance(dbHelper)
                            .removeBook(uri.getLastPathSegment());
                rootUri = Contract.Book.CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }
        notifyUris(uri, rootUri);
        return rowCount;
    }


     public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
         int rowCount;

         final int code = URI_MATCHER.match(uri);
         switch (code) {

             case CODE_BOOK_ID:
                 if (selection == null
                         && selectionArgs == null) {
                     selection = BaseColumns._ID + " = ?";

                     selectionArgs = new String[] {
                             uri.getLastPathSegment()
                     };
                 } else {
                     throw new IllegalArgumentException("Selection must be " +
                             "null when specifying ID as part of uri.");
                 }
                 rowCount = dbHelper
                         .getWritableDatabase()
                         .update(URI_CODE_TABLE_MAP.get(code),
                                 values,
                                 selection,
                                 selectionArgs);
                 break;
             default:
                 throw new IllegalArgumentException("Invalid Uri: " + uri);
         }

         notifyUris(uri);
         return rowCount;
    }

    private void notifyUris(Uri affectedUri) {
        notifyUris(affectedUri, null);
    }

    private void notifyUris(Uri affectedUri, Uri rootUri) {
        Log.i(TAG, "notifyUris");
        final ContentResolver contentResolver = getContext().getContentResolver();
        if (contentResolver != null) {
            Log.i(TAG, "nofifyUris: " + affectedUri + " root:" + rootUri);
            contentResolver.notifyChange(affectedUri, null);
            if(rootUri != null)
                contentResolver.notifyChange(rootUri, null);
        }
    }
}
