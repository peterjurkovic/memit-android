package io.memit.android.provider;

import android.provider.BaseColumns;

/**
 * Created by peter on 1/29/17.
 */

public class BaseContract {


    public interface SyncColumns extends BaseColumns {
        public static final String SID = "sid";
        public static final String CHANGED = "changed";
        public static final String CREATED = "created";
    }

}
