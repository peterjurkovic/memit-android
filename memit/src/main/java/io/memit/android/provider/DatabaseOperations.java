package io.memit.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.memit.android.BuildConfig;
import io.memit.android.activity.memit.MemitSession;
import io.memit.android.activity.memit.Rating;
import io.memit.android.provider.Contract.Lecture;
import io.memit.android.provider.Contract.SessionWord;
import io.memit.android.provider.Contract.Word;

import static io.memit.android.provider.Contract.MemiStrategy.SEQUENCE;
import static io.memit.android.tools.CursorUtils.asInt;

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

    public boolean bookExists(String name, String id){
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Checking whether book with [name=" + name+ "] [id=" + id + "] bookExists");
        String selection;
        String[] selectionArgs;
        if(id == null){
            selection =  "name=? AND deleted = 0 ";
            selectionArgs = new String[]{ String.valueOf(name) };
        }else{
            selection =  "name=? AND deleted = 0 AND _id<>?  ";
            selectionArgs = new String[]{ String.valueOf(name), id };
        }

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

    public Cursor getLectureById(String lectureId){
        StringBuilder query = new StringBuilder()
                .append("SELECT l.*, b.name as bookName ")
                .append("FROM book b " )
                .append("   JOIN lecture l ON l.book_id = b._id ")
                .append("WHERE l._id = '").append(lectureId).append("'");
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.rawQuery(query.toString(), null );
    }


    public Cursor getAllBooks(){
        StringBuilder query = new StringBuilder()
                .append("SELECT b._id, b.name, ")
                .append("   COUNT(DISTINCT l._id) as lecture_count, " )
                .append("   IFNULL(COUNT(DISTINCT w._id),0) as word_count, " )
                .append("   IFNULL(SUM( w.active),0) as active_word_count " )
                .append("FROM book b " )
                .append("   LEFT JOIN lecture l ON l.book_id = b._id AND l.deleted = 0 ")
                .append("   LEFT JOIN word w ON w.lecture_id = l._id AND w.deleted = 0 ")
                .append("WHERE b.deleted = 0 ")
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
        String now = now(db);
        ContentValues deleted = new ContentValues();
        deleted.put(Contract.SyncColumns.DELETED, 1);
        deleted.put(Contract.SyncColumns.CHANGED, now);
        final int updatedCount = db.update(Contract.Book.TABLE, deleted, BaseColumns._ID+"=?", selectionArgs);
        if(updatedCount > 0) {
            List<Long> ids = getLecturesId(db, selectionArgs);
            if(!ids.isEmpty()){
                String inClause = asINClause(ids);
                db.execSQL("UPDATE " + Word.TABLE+ " SET deleted = 1, changed='"+now+"' WHERE `lecture_id` IN " + inClause);
                db.execSQL("UPDATE " + Lecture.TABLE + " SET deleted = 1, changed='"+now+"' WHERE `book_id`= ? ", selectionArgs);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        try {
            return updatedCount;
        }finally {
            db.close();
        }
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

    public Cursor getAllLectures( String bookId ){
        if(bookId == null){
            Log.e(TAG, "Unexpected NULL bookId");
            bookId = "";
        }
        StringBuilder query = new StringBuilder()
            .append("SELECT l._id, l.name, l.lang_question, l.lang_answer, ")
            .append("   IFNULL(COUNT( DISTINCT w._id ),0) as word_count, ")
            .append("   IFNULL(SUM( w.active ),0) as active_word_count ")
            .append("FROM lecture l ")
            .append("LEFT JOIN word w ON w.lecture_id = l._id AND w.deleted = 0 ")
            .append("WHERE l.book_id = ? AND l.deleted = 0 ")
            .append("GROUP BY l._id ")
            .append("ORDER BY l.name ");

        SQLiteDatabase db = helper.getWritableDatabase();
        return db.rawQuery(query.toString(), new String[]{bookId } );
    }

    public int removeLecture(String id){
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Removing book: " + id);

        String[] selectionArgs = { String.valueOf(id) };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        String now = now(db);
        db.execSQL("UPDATE " + Word.TABLE+ " SET deleted = 1, changed='"+now+"' WHERE `lecture_id` = ?", selectionArgs);
        db.execSQL("UPDATE " + Lecture.TABLE + " SET deleted = 1, changed='"+now+"' WHERE `_id`= ? ", selectionArgs);
        db.setTransactionSuccessful();
        db.endTransaction();
        return 1;
    }


    public Cursor loadSessionMemos(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "" +
                " SELECT w._id, w.question, w.answer, l.lang_question, l.lang_answer, ifnull(sw.hits,0) as hits " +
                "        FROM word w " +
                "        LEFT JOIN lecture l ON w.lecture_id = l._id " +
                "        LEFT JOIN session_word sw ON sw.word_id = w._id " +
                "        WHERE w.active = 1 AND w.deleted = 0 " +
                "        ORDER BY sw.changed DESC, w.changed DESC " +
                "        LIMIT " + (MemitSession.QUEUE_SIZE_LIMIT + 1);
        return db.rawQuery(sql, null);
    }

    public Cursor loadNextMemo(int strategy, String excludeWordsClause){
        SQLiteDatabase db = helper.getReadableDatabase();


        StringBuilder sql = new StringBuilder()
            .append(" SELECT w._id, w.question, w.answer, l.lang_question, l.lang_answer, ifnull(sw.hits,0) as hits " )
            .append("FROM word w " )
            .append("LEFT JOIN lecture l ON w.lecture_id = l._id " )
            .append("LEFT JOIN session_word sw ON sw.word_id = w._id " );

        switch (strategy){
            case SEQUENCE :
                sql.append("WHERE w.active = 1 AND w.deleted = 0 ")
                   .append(excludeWordsClause)
                   .append(" ORDER BY sw.changed DESC, w.changed DESC ")
                   .append(" LIMIT 1 ");
                break;
        }


        return db.rawQuery(sql.toString(), null);
    }

    public int createOrUpdateSessionWord(ContentValues cv){
        SQLiteDatabase db = helper.getReadableDatabase();
        db.beginTransaction();
        deactivate(db, cv);
        updateSessionWord(db, cv);
        db.setTransactionSuccessful();
        db.endTransaction();
        return 0;
    }

    private void deactivate(SQLiteDatabase db, ContentValues cv){
        int lastRating = cv.getAsInteger(SessionWord.LAST_RATING);
        if(lastRating == Rating.KNOW.value()){
            String wordId = cv.getAsString(SessionWord.WORD_ID);
            ContentValues vals = new ContentValues();
            vals.put(Word.ACTIVE, 0);
            vals.put(Word.CHANGED, cv.getAsString(Word.CHANGED));
            db.update(Word.TABLE, vals , Word._ID + " = ?" , new String[]{wordId});
            Log.i("Sessionmanager", "Deactivated: " + wordId);
        }
    }

    private void updateSessionWord(SQLiteDatabase db, ContentValues cv){
        String sessionId = cv.getAsString(SessionWord.SESSION_ID);
        String wordId = cv.getAsString(SessionWord.WORD_ID);
        String now = cv.getAsString(Contract.SyncColumns.CHANGED);
        int lastRating = cv.getAsInteger(SessionWord.LAST_RATING);
        Cursor cursor = db.rawQuery(
                "SELECT count(*) as c "+
                "FROM session_word "+
                "WHERE word_id = '"+wordId+"' AND session_id = '"+sessionId+"' ", null);

        boolean exists = false;
        if(cursor.moveToNext()){
            exists = asInt(cursor, "c") > 0;
        }

        if(exists){
            ContentValues insertCv = new ContentValues();
            insertCv.put(SessionWord.WORD_ID, wordId);
            insertCv.put(SessionWord.SESSION_ID, sessionId);
            insertCv.put(SessionWord.CHANGED, now);
            insertCv.put(SessionWord.CREATED, now);
            insertCv.put(SessionWord.LAST_RATING, lastRating);
            insertCv.put(SessionWord.RATE_SUM, lastRating);
            db.insert(SessionWord.TABLE, null, insertCv);
            Log.i("Sessionmanager", "sessionWord created");
        }else{
            StringBuilder sql = new StringBuilder("UPDATE session_word SET ")
                .append( "hits = hits+1, ")
                .append( "rate_sum = rate_sum + ").append(lastRating).append(", ")
                .append( "changed = '").append(now).append("' ")
                .append( "WHERE word_id = '").append(wordId).append("' AND session_id = '")
                .append(sessionId).append("'");

            db.execSQL(sql.toString());
            Log.i("Sessionmanager", "sessionWord updated" + sql);
        }

    }
/*


    private Memo updateAndGetNext(Memo memo, int rating){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        String now =  now(db);
        db.up

        db.setTransactionSuccessful();

    }


    public int getNumberOfActiveCards(SQLiteDatabase db){
        Cursor c = db.rawQuery("SELECT count(*) as active FROM word WHERE active=1 AND deleted=0", null);
        try{
            if(c.moveToNext()){
                return asInt(c, "active");
            }
        }finally {
            c.close();
        }
        return 0;
    }

*/

}
