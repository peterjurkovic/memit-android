package io.memit.android.provider;

import android.content.ContentResolver;
import android.net.Uri;

import io.memit.android.BuildConfig;

/**
 * Created by peter on 1/29/17.
 */

public class Contract {

    public static final String AUTHORITY = String.format("%s.provider", BuildConfig.APPLICATION_ID);

    public static final Uri AUTHORITY_URI = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY)
            .build();




    public interface Book extends BaseContract.SyncColumns {
        static final String PATH = "book";
        static final String NAME = "name";
        static final String LANG_QUESTION = "lang_question";
        static final String LANG_ANSWER = "lang_answer";
        static final String LEVEL = "level";
        // static final String PUBLISHED = "pulished";


        static final Uri CONTENT_URI =  Uri.withAppendedPath(AUTHORITY_URI, PATH);

    }

    public interface Lecture extends BaseContract.SyncColumns{
        static final String PATH = "lecture";
        static final String NAME = "name";
        static final String LANG_QUESTION = "lang_question";
        static final String LANG_ANSWER = "lang_answer";
        static final String BOOK_ID = "book_id";
        static final Uri CONTENT_URI =  Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static String[] allBookColumns(){
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
