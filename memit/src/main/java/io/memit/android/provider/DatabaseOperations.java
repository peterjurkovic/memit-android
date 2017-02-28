package io.memit.android.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.memit.android.BuildConfig;
import io.memit.android.provider.Contract.Lecture;
import io.memit.android.provider.Contract.Word;

import static java.lang.String.valueOf;

/**
 * Created by peter on 1/30/17.
 */

public class DatabaseOperations extends BaseDatabaseOperations {


    public final static String TAG = DatabaseOperations.class.getName();

    private static final Object LOCK = new Object();
    private static DatabaseOperations OPERATIONS;


    private DatabaseOperations(final DatabaseOpenHelper helper){
        super(helper);
    }

    public static DatabaseOperations getInstance(Context context){
        return getInstance(DatabaseOpenHelper.getInstance(context));
    }

    public static DatabaseOperations getInstance(final DatabaseOpenHelper helper){
        if(OPERATIONS == null){
            synchronized (LOCK){
                if(OPERATIONS == null){
                    OPERATIONS = new DatabaseOperations(helper);
                }
            }
        }
        return OPERATIONS;
    }

    public boolean bookExists(String name, long id){
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Checking whether book with [name=" + name+ "] [id=" + id + "] bookExists");

        String selection =  "name=? AND _id<>?";
        String[] selectionArgs = { String.valueOf(name), String.valueOf(id) };
        Cursor cursor = null;
        try {
            cursor = helper.getWritableDatabase()
                    .query(Contract.Book.TABLE, new String[]{BaseColumns._ID}, selection, selectionArgs, null, null, null);
            return cursor.moveToNext();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }

    }


    public Cursor getAllBooks(){
        StringBuilder query = new StringBuilder()
                .append("SELECT b._id, b.name, ")
                .append("   COUNT(DISTINCT l._id) as lecture_count, " )
                .append("   IFNULL(COUNT(DISTINCT w._id),0) as word_count, " )
                .append("   IFNULL(SUM( w.active),0)as active_word_count " )
                .append("FROM book b " )
                .append("   LEFT JOIN lecture l ON l.book_id = b._id ")
                .append("   LEFT JOIN word w ON w.lecture_id = l._id ")
                .append("GROUP BY b._id ")
                .append("ORDER BY b.name");
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.rawQuery(query.toString(), null );
    }

    public int removeBook(String id){
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Removing book: " + id);

        String[] selectionArgs = { String.valueOf(id) };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        final int deletedCount = db.delete(Contract.Book.TABLE, BaseColumns._ID+"=?", selectionArgs);
        if(deletedCount > 0) {
            List<Long> ids = getLecturesId(db, selectionArgs);
            if(!ids.isEmpty()){
                String inClause = asINClause(ids);
                db.execSQL("DELETE FROM " + Word.TABLE+ " WHERE `lecture_id` IN " + inClause);
                db.execSQL("DELETE FROM " + Lecture.TABLE + " WHERE `book_id`= ? ", selectionArgs);
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

    public Cursor getAllLectures( long bookId ){

        StringBuilder query = new StringBuilder()
            .append("SELECT l._id, l.name, l.lang_question, l.lang_answer, ")
            .append("   IFNULL(COUNT( DISTINCT w._id ),0) as word_count, ")
            .append("   IFNULL(SUM( w.active ),0) as active_word_count ")
            .append("FROM lecture l ")
            .append("LEFT JOIN word w ON w.lecture_id = l._id ")
            .append("WHERE l.book_id = ? ")
            .append("GROUP BY l._id ")
            .append("ORDER BY l.name ");

        SQLiteDatabase db = helper.getWritableDatabase();
        return db.rawQuery(query.toString(), new String[]{valueOf(bookId )} );
    }

    public int removeLecture(String id){
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Removing book: " + id);

        String[] selectionArgs = { String.valueOf(id) };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        db.execSQL("DELETE FROM " + Word.TABLE+ " WHERE `lecture_id` = ?", selectionArgs);
        db.execSQL("DELETE FROM " + Lecture.TABLE + " WHERE `_id`= ? ", selectionArgs);
        db.setTransactionSuccessful();
        db.endTransaction();
        return 1;
    }
}
