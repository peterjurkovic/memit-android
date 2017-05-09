package io.memit.android.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import io.memit.android.BuildConfig;

import static android.net.Uri.withAppendedPath;

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
        String DELETED = "deleted";
        String CHANGED = "changed";
        String CREATED = "created";

        String LECTURE_COUNT = "lecture_count";
        String WORD_COUNT = "word_count";
        String ACTIVE_WORD_COUNT = "active_word_count";
    }

    public interface Book extends SyncColumns {
        String TABLE = "book";
        String PATH = "books";

        String NAME = "name";
        String LANG_QUESTION = "lang_question";
        String LANG_ANSWER = "lang_answer";
        String LEVEL = "level";

        Uri CONTENT_URI =  withAppendedPath(AUTHORITY_URI, PATH);

    }

    public interface Lecture extends SyncColumns{
        String TABLE = "lecture";
        String PATH = "lectures";

        String NAME = "name";
        String LANG_QUESTION = "lang_question";
        String LANG_ANSWER = "lang_answer";
        String BOOK_ID = "book_id";
        // static final Uri CONTENT_URI =  Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public interface Word extends SyncColumns{
        Uri CONTENT_URI =  withAppendedPath(AUTHORITY_URI, "words");
        String TABLE = "word";

        String QUESTION = "question";
        String ANSWER = "answer";
        String ACTIVE = "active";
        String LECTURE_ID = "lecture_id";
    }

    public interface  Session extends SyncColumns{
        final Uri INIT_URI =  withAppendedPath(AUTHORITY_URI, "sessions/init");
        final Uri CONTENT_URI =  withAppendedPath(AUTHORITY_URI, "sessions");
        final String TABLE = "session";
    }

    public interface SessionWord extends SyncColumns{
        final Uri URI =  withAppendedPath(AUTHORITY_URI, "sessions");
        final Uri RATE_URI =  withAppendedPath(URI, "rate");
        String TABLE = "session_word";
        String SESSION_ID = "session_id";
        String WORD_ID = "word_id";
        String LAST_RATING = "last_rating";
        String RATE_SUM = "rate_sum";
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


    public  interface MemiStrategy{
        byte SEQUENCE = 1;
        byte RANDOM = 2;
        byte PROBLEMATIC = 3;


        Uri SEQUENCE_STRATEGY_URI =  withAppendedPath(AUTHORITY_URI, "sessions/next/" + SEQUENCE);

    }


}
