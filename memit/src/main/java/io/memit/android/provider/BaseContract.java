package io.memit.android.provider;

import android.provider.BaseColumns;

/**
 * Created by peter on 1/29/17.
 */

public class BaseContract {


    public interface SyncColumns extends BaseColumns {
        static final String SID = "sid";
        static final String CHANGED = "changed";
        static final String CREATED = "created";

        static final String LECTURE_COUNT = "lecture_count";
        static final String WORD_COUNT = "word_count";
        static final String ACTIVE_WORD_COUNT = "active_word_count";
    }

}
