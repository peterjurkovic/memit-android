package io.memit.android.activity.memit;

/**
 * Created by peter on 5/7/17.
 */

public enum Rating {

    KNOW(1),
    NEUTRAL(3),
    DONT_KNOW(5);

    private final byte value;

    private Rating(int id) {
        this.value = (byte) id;
    }

    public int value() {
        return value;
    }

    ;
}
