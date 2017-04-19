package io.memit.android.tools;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by peter on 3/13/17.
 */

public class HashSetMultiSelector {

    private Set<String> ids = new HashSet<>(32);

    public boolean isSelectable() {
        return false;
    }

    public int getSelectedCount() {
        return ids.size();
    }

    public boolean isSelected(String id) {
        return ids.contains(id);
    }

    public void setSelected(String val) {
        ids.add(val);
    }

    public void removeAt(String id) {
        ids.remove(id);
    }

    public ArrayList<String> getAllIds() {
        return new ArrayList<>(ids);
    }


    public void clear(){
        ids.clear();
    }

}
