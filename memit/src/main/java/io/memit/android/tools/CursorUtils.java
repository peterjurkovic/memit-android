package io.memit.android.tools;

import android.database.Cursor;

/**
 * Created by peter on 2/22/17.
 */

public class CursorUtils {


    public static String asString(Cursor c, String column){
        if(c != null){
            return c.getString(c.getColumnIndexOrThrow(column));
        }
        throw new IllegalArgumentException("Curor is already closed. [column="+column+"]");
    }

    public static int asInt(Cursor c, String column){
        if(c != null){
            return c.getInt(c.getColumnIndexOrThrow(column));
        }
        throw new IllegalArgumentException("Curor is already closed. [column="+column+"]");
    }
}
