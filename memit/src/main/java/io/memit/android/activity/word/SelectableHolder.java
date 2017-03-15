package io.memit.android.activity.word;

/**
 * Created by peter on 3/11/17.
 */

public interface SelectableHolder {

    void setSelectable(boolean selectable);

    boolean isSelectable();

    void setActivated(boolean activated);

    boolean isActivated();

    int getPosition();

    long getItemId();
}
