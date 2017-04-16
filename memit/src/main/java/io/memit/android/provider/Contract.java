package io.memit.android.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

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



    public interface SyncColumns extends BaseColumns {
        static final String DELETED = "deleted";
        static final String CHANGED = "changed";
        static final String CREATED = "created";

        static final String LECTURE_COUNT = "lecture_count";
        static final String WORD_COUNT = "word_count";
        static final String ACTIVE_WORD_COUNT = "active_word_count";
    }

    public interface Book extends SyncColumns {
        static final String TABLE = "book";
        static final String PATH = "books";

        static final String NAME = "name";
        static final String LANG_QUESTION = "lang_question";
        static final String LANG_ANSWER = "lang_answer";
        static final String LEVEL = "level";
        // static final String PUBLISHED = "pulished";


        static final Uri CONTENT_URI =  Uri.withAppendedPath(AUTHORITY_URI, PATH);

    }

    public interface Lecture extends SyncColumns{
        static final String TABLE = "lecture";
        static final String PATH = "lectures";

        static final String NAME = "name";
        static final String LANG_QUESTION = "lang_question";
        static final String LANG_ANSWER = "lang_answer";
        static final String BOOK_ID = "book_id";
        // static final Uri CONTENT_URI =  Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public interface Word extends SyncColumns{
        static final String TABLE = "word";

        static final String QUESTION = "question";
        static final String ANSWER = "answer";
        static final String ACTIVE = "active";
        static final String LECTURE_ID = "lecture_id";
    }


    public static String[] allBookColumns(){
        return new String[]{
                Book._ID,
                Book.NAME,
                Book.LANG_ANSWER,
                Book.LANG_QUESTION,
                Book.LEVEL
        };
    }

    public static String[] allLectureColumns(){
        return new String[]{
                Lecture._ID,
                Lecture.NAME,
                Lecture.LANG_ANSWER,
                Lecture.LANG_QUESTION
        };
    }

    public static String[] getWordsColumns(){
        return new String[]{
                Word._ID,
                Word.QUESTION,
                Word.ANSWER,
                Word.ACTIVE
        };
    }





}
