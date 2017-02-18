package io.memit.android.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static java.lang.String.valueOf;

/**
 * Created by peter on 1/30/17.
 */

public class LectureDatabaseOperation extends BaseDatabaseOperations {

    private static final Object LOCK = new Object();
    private static LectureDatabaseOperation OPERATIONS;


    private LectureDatabaseOperation( final DatabaseOpenHelper helper){
        super(helper);
    }

    public static LectureDatabaseOperation getInstance(Context context){
        return getInstance(DatabaseOpenHelper.getInstance(context));
    }

    public static LectureDatabaseOperation getInstance(final DatabaseOpenHelper helper){
        if(OPERATIONS == null){
            synchronized (LOCK){
                if(OPERATIONS == null){
                    OPERATIONS = new LectureDatabaseOperation(helper);
                }
            }
        }
        return OPERATIONS;
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
}
