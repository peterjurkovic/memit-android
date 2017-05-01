package io.memit.android.activity.memit;

/**
 * Created by peter on 4/27/17.
 */

public interface MemitStrategy {

    Memo getNext(MemitSession session);

}
