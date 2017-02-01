package io.memit.android.provider;

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


}
