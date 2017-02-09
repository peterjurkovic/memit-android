package io.memit.android.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import io.memit.android.BuildConfig;

/**
 * Created by peter on 1/29/17.
 */

public class BookContract {

    public static final String AUTHORITY = String.format("%s.provider", BuildConfig.APPLICATION_ID);

    public static final Uri AUTHORITY_URI = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY)
            .build();

    public static final Uri CONTENT_URI =
            Uri.withAppendedPath(AUTHORITY_URI, Book.PATH);

    static {
        if(BuildConfig.DEBUG){
            Log.d(BookContract.class.getName(), "AUTHORITY_URI: " +AUTHORITY_URI );
            Log.d(BookContract.class.getName(), "CONTENT_URI: " +CONTENT_URI );
        }
    }

    public interface Book extends BaseContract.SyncColumns {
            static final String PATH = "book";
            public static final String NAME = "name";
            public static final String LANG_QUESTION = "lang_question";
            public static final String LANG_ANSWER = "lang_answer";
            public static final String LEVEL = "level";
            public static final String PUBLISHED = "pulished";

            public static final Uri CONTENT_URI =  Uri.withAppendedPath(AUTHORITY_URI, PATH);



    }

    public static String[] allColumns(){
        return new String[]{
                Book._ID,
                Book.SID,
                Book.NAME,
                Book.LANG_ANSWER,
                Book.LANG_QUESTION,
                Book.LEVEL
        };
    }



}
