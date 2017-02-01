package io.memit.android.provider;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.memit.android.BuildConfig;
import io.memit.android.provider.DatabaseOpenHelper.Tables;

/**
 * Created by peter on 1/30/17.
 */

public class BookDatabaseOperations extends  BaseDatabaseOperations{

    private final static String TAG = BookDatabaseOperations.class.getSimpleName();
    private static final Object lock = new Object();
    private static BookDatabaseOperations operations;


    private BookDatabaseOperations(DatabaseOpenHelper helper){
        super(helper);
    }

    public static BookDatabaseOperations getInstance(final DatabaseOpenHelper helper){
        if(operations == null){
            synchronized (lock){
                if(operations == null){
                    operations = new BookDatabaseOperations(helper);
                }
            }
        }
        return operations;
    }

    public int removeBook(String id){
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Removing book: " + id);

        String[] selectionArgs = { String.valueOf(id) };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        final int deletedCount = db.delete(Tables.BOOK, BaseColumns._ID+"=?", selectionArgs);
        if(deletedCount > 0) {
            List<Long> ids = getLecturesId(db, selectionArgs);
            if(!ids.isEmpty()){
                String inClause = asINClause(ids);
                db.execSQL("DELETE FROM " + Tables.WORD+ " WHERE `lecture_id` IN " + inClause);
                db.execSQL("DELETE FROM " + Tables.LECTURE+ " WHERE `book_id`= ? ", selectionArgs);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return deletedCount;
    }

    private List<Long> getLecturesId(SQLiteDatabase db, String[] selectionArgs ){
        List<Long> ids = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT _id FROM `lecture` WHERE `book_id` = ?", selectionArgs);
        try{
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(BaseColumns._ID);
            ids.add( cursor.getLong(index) );
        }
        }finally {
            if(cursor != null) cursor.close();
        }
        return ids;
    }
}
