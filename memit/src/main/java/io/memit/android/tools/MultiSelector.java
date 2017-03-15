package io.memit.android.tools;

import java.util.Collection;

/**
 * Created by peter on 3/13/17.
 */

interface MultiSelector {
    boolean isSelectable();

    int getSelectedCount();

    boolean isSelected(int position);

    void toggleSelection(int position, int val);

    void setSelected(int position, int val);

    void removeAt(int position);

    Collection<String> getAllIds();
}
