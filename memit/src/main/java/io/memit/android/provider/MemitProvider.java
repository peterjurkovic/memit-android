package io.memit.android.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
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

import java.util.UUID;

import io.memit.android.BuildConfig;
import io.memit.android.provider.Contract.Book;
import io.memit.android.provider.Contract.Lecture;
import io.memit.android.provider.Contract.Session;
import io.memit.android.provider.Contract.Word;
import io.memit.android.tools.UriUtils;

/**
 * Created by peter on 1/29/17.
 */

public class MemitProvider extends ContentProvider{

    private static final String TAG = MemitProvider.class.getSimpleName();

    private DatabaseOpenHelper dbHelper;


    private static final byte BOOKS = 1;
    private static final byte BOOK_ID = 2;
    private static final byte BOOK_LECTURES = 3;
    private static final byte BOOK_LECTURES_ID = 4;
    private static final byte BOOK_LECTURES_WORDS = 5;
    private static final byte BOOK_LECTURES_WORDS_ID = 6;
    private static final byte SESSIONS = 7;
    private static final byte SESSIONS_INIT = 8;

    private static final SparseArray<String> CODE_TABLE_MAP = new SparseArray<>();
    private static final UriMatcher URI_MATCHER =  new UriMatcher(UriMatcher.NO_MATCH);

    static {
        CODE_TABLE_MAP.put(BOOK_ID, Book.TABLE);
        CODE_TABLE_MAP.put(BOOK_LECTURES_ID, Lecture.TABLE);
        CODE_TABLE_MAP.put(BOOKS, Book.TABLE);
        CODE_TABLE_MAP.put(BOOK_LECTURES_ID, Lecture.TABLE);
        CODE_TABLE_MAP.put(BOOK_LECTURES, Lecture.TABLE);
        CODE_TABLE_MAP.put(BOOK_LECTURES_WORDS, Word.TABLE);
        CODE_TABLE_MAP.put(BOOK_LECTURES_WORDS_ID, Word.TABLE);
        CODE_TABLE_MAP.put(SESSIONS, Session.TABLE);


        URI_MATCHER.addURI(Contract.AUTHORITY, "books", BOOKS);
        URI_MATCHER.addURI(Contract.AUTHORITY, "books/*", BOOK_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY, "books/*/lectures", BOOK_LECTURES);
        URI_MATCHER.addURI(Contract.AUTHORITY, "books/*/lectures/*", BOOK_LECTURES_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY, "books/*/lectures/*/words", BOOK_LECTURES_WORDS);
        URI_MATCHER.addURI(Contract.AUTHORITY, "books/*/lectures/*/words/*", BOOK_LECTURES_WORDS_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY, "sessions", SESSIONS);
        URI_MATCHER.addURI(Contract.AUTHORITY, "sessions/activity", SESSIONS_INIT);
    }


    @Override
    public boolean onCreate() {
        dbHelper = DatabaseOpenHelper.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public  Cursor query(@NonNull Uri uri,
                                String[] columns,
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
            case SESSIONS_INIT:
                return DatabaseOperations
                        .getInstance(getContext())
                        .loadSessionMemos();
           case BOOKS:
                return DatabaseOperations
                        .getInstance(getContext())
                        .getAllBooks();

            case BOOK_LECTURES:
                String bookId = UriUtils.getBookId(uri);
                return DatabaseOperations
                        .getInstance(getContext())
                        .getAllLectures(bookId);
            case BOOK_LECTURES_ID:
                return  DatabaseOperations
                        .getInstance(getContext())
                        .getLectureById(UriUtils.getLectureId(uri));
            case BOOK_ID:
                if (selection == null) {
                    selection = BaseColumns._ID + " = '" + uri.getLastPathSegment() + "'";
                } else {
                    throw new IllegalArgumentException("Selection must " +
                            "be null when specifying ID as part of uri.");
                }
        }

        if(isSyncableEntity(code)){
            if(selection == null){
                selection = "deleted=0";
            }else if(! selection.contains("deleted")){
                selection += " AND deleted=0";
            }
        }
        // (String table, String[] columns, String selection,
        // String[] selectionArgs, String groupBy, String having, String orderBy

        return database.query(CODE_TABLE_MAP.get(code),
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "[getType] uri: " + uri);
        final int code = URI_MATCHER.match(uri);
        switch (code) {
            case BOOKS:
                return String.format("%s/vnd.%s.%s",
                        ContentResolver.CURSOR_DIR_BASE_TYPE,
                        Contract.AUTHORITY,
                        Book.PATH);
            case BOOK_ID:
                return String.format("%s/vnd.%s.%s",
                        ContentResolver.CURSOR_ITEM_BASE_TYPE,
                        Contract.AUTHORITY,
                        Book.PATH);
            default:
                return null;
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {


        Log.d(TAG, "Creating [uri="+uri+"] values: " + values);
        final int code = URI_MATCHER.match(uri);
        switch (code) {
            case BOOKS:
            case BOOK_LECTURES:
            case BOOK_LECTURES_WORDS:
                String id = UUID.randomUUID().toString();
                values.put(BaseColumns._ID, id);

                dbHelper.getWritableDatabase()
                        .insertOrThrow(CODE_TABLE_MAP.get(code), null, values);
                Log.i(TAG, "New ID: " + id+ " generated");
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }

        notifyUris(uri);
        return UriUtils.withAppendedId(uri, values.getAsString(BaseColumns._ID));
    }


    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {
        int rowCount;
        final int code = URI_MATCHER.match(uri);
        switch (code) {
            case BOOK_ID:
                return DatabaseOperations
                    .getInstance(dbHelper)
                    .removeBook(uri.getLastPathSegment());
            case BOOK_LECTURES_ID:
                return DatabaseOperations
                    .getInstance(dbHelper)
                    .removeLecture(uri.getLastPathSegment());
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if(isSyncableEntity(code)){
            ContentValues cv = DatabaseOperations.deletedContentValues(db);
            rowCount = db.update(CODE_TABLE_MAP.get(code), cv, whereClause, whereArgs);
        }else{
            rowCount = db.delete(CODE_TABLE_MAP.get(code), whereClause, whereArgs);
        }
        notifyUris(uri);
        return rowCount;
    }


     public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
         int rowCount;
         final int code = URI_MATCHER.match(uri);
         final String table = CODE_TABLE_MAP.get(code);
         SQLiteDatabase db = dbHelper.getWritableDatabase();
         String now = DatabaseOperations.now(db);
         values.put(Contract.SyncColumns.CHANGED, now);
         switch (code) {
             case BOOK_LECTURES_WORDS_ID:
             case BOOK_LECTURES_ID:
             case BOOK_ID:
                 if (selection == null && selectionArgs == null) {
                     selection = BaseColumns._ID + " = ?";
                     selectionArgs = new String[] { uri.getLastPathSegment()};
                 } else {
                     throw new IllegalArgumentException("Selection must be " +
                             "null when specifying ID as part of uri.");
                 }
                 rowCount = db.update(CODE_TABLE_MAP.get(code),
                                 values,
                                 selection,
                                 selectionArgs);
                 break;
             case BOOK_LECTURES_WORDS:
                 return db.update(table, values, selection, selectionArgs);

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
        final ContentResolver contentResolver = getContext().getContentResolver();
        if (contentResolver != null) {
            contentResolver.notifyChange(affectedUri, null);
            if(rootUri != null)
                contentResolver.notifyChange(rootUri, null);
        }
    }


    private static boolean isSyncableEntity(int code){
        return code <= SESSIONS_INIT;
    }
}
