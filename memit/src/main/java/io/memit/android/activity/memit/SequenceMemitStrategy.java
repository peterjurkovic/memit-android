package io.memit.android.activity.memit;

import android.content.AsyncQueryHandler;
import android.content.Context;

/**
 * Created by peter on 4/27/17.
 */

public class SequenceMemitStrategy extends AsyncQueryHandler  {


    public SequenceMemitStrategy(Context context) {
        super(context.getContentResolver());
    }


    public Memo getFirst() {
        return null;
    }

    public Memo getNext(MemitSession session) {
        return null;
    }
}
