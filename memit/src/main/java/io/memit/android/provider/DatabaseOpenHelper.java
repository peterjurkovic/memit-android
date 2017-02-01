package io.memit.android.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.memit.android.BuildConfig;

/**
 * Created by peter on 1/28/17.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseOpenHelper.class.getSimpleName();
    private static final int SCHEMA_VERSION = 1;
    private static final String DB_NAME = "memit.db";

    private final Context context;
    private static Object lock = new Object();
    private static DatabaseOpenHelper instance;

    private DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA_VERSION);
        this.context = context;

        // This will happen in onConfigure for API >= 16
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            SQLiteDatabase db = getWritableDatabase();
            db.enableWriteAheadLogging();
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }

    public static DatabaseOpenHelper getInstance(Context context){
        if(instance == null){
            synchronized (lock){
                if (instance == null) {
                    instance = new DatabaseOpenHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 1; i <= SCHEMA_VERSION; i++) {
            applySqlFile(db, i);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = (oldVersion + 1); i <= newVersion; i++) {
            applySqlFile(db, i);
        }
    }

    private void applySqlFile(SQLiteDatabase db, int version) {
        BufferedReader reader = null;

        try {
            String filename = String.format("%s.%d.sql", DB_NAME, version);
            final InputStream inputStream = context.getAssets().open(filename);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            Log.i(TAG, filename);
            final StringBuilder statement = new StringBuilder();

            for (String line; (line = reader.readLine()) != null;) {
                 if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Reading line -> " + line);
                }

                // Ignore empty lines
                if (!TextUtils.isEmpty(line) && !line.startsWith("--")) {
                    statement.append(line.trim());
                }

                if (line.endsWith(";")) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Running statement " + statement);
                    }
                    Log.d(TAG, "Running statement " + statement);
                    db.execSQL(statement.toString());
                    statement.setLength(0);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "Could not apply SQL file", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.w(TAG, "Could not close reader", e);
                }
            }
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        setWriteAheadLoggingEnabled(true);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public interface Tables {
        String BOOK = "book";
        String LECTURE = "lecture";
        String WORD = "word";
    }
}
