package io.memit.android.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by peter on 1/30/17.
 */

public class BaseDatabaseOperations {


    protected final DatabaseOpenHelper helper;

    protected BaseDatabaseOperations(DatabaseOpenHelper helper){
        this.helper = helper;
    }



    protected static String asINClause(List<Long> ids){
        return new StringBuilder(" IN (")
            .append(TextUtils.join(",", ids))
            .append(")").toString();

    }
    protected static String now(SQLiteDatabase db){
        Cursor c = db.rawQuery("SELECT datetime() as now ", null);
        try{
            if(c.moveToNext()){
                return c.getString(0);
            }
        }finally {
            if(c != null){
                c.close();
            }
        }
        throw new IllegalStateException("Could not acquire current timestemp");
    }

    protected static ContentValues deletedContentValues(SQLiteDatabase db){
        String now = now(db);
        ContentValues cv = new ContentValues();
        cv.put(Contract.SyncColumns.DELETED, 1);
        cv.put(Contract.SyncColumns.CHANGED, now);
        return cv;
    }


}
