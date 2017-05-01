package io.memit.android.tools;

import android.database.Cursor;

/**
 * Created by peter on 2/22/17.
 */

public class CursorUtils {

    public static short asShort(Cursor c, String column){
        if(c != null){
            return c.getShort(c.getColumnIndexOrThrow(column));
        }
        throw new IllegalArgumentException("Cursor is already closed. [column="+column+"]");
    }

    public static String asString(Cursor c, String column){
        if(c != null){
            return c.getString(c.getColumnIndexOrThrow(column));
        }
        throw new IllegalArgumentException("Cursor is already closed. [column="+column+"]");
    }

    public static int asInt(Cursor c, String column){
        if(c != null){
            return c.getInt(c.getColumnIndexOrThrow(column));
        }
        throw new IllegalArgumentException("Cursor is already closed. [column="+column+"]");
    }

    public static long asLong(Cursor c, String column){
        if(c != null){
            return c.getLong(c.getColumnIndexOrThrow(column));
        }
        throw new IllegalArgumentException("Cursor is already closed. [column="+column+"]");
    }

    public static boolean asBool(Cursor c, String column){
        if(c != null){
            return c.getInt(c.getColumnIndexOrThrow(column)) == 1;
        }
        throw new IllegalArgumentException("Cursor is already closed. [column="+column+"]");
    }

    public static boolean asBoolean(Cursor c, String column){
        return asInt(c, column) == 1;
    }
}
