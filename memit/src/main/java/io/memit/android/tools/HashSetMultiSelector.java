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

    private Set<Integer> ids = new HashSet<>(64);

    public boolean isSelectable() {
        return false;
    }

    public int getSelectedCount() {
        return ids.size();
    }

    public boolean isSelected(int id) {
        return ids.contains(Integer.valueOf(id));
    }

    public void setSelected(int val) {
        ids.add(Integer.valueOf(val));
    }

    public void removeAt(int id) {
        ids.remove(Integer.valueOf(id));
    }

    public ArrayList<Integer> getAllIds() {
        return new ArrayList<>(ids);
    }


    public void clear(){
        ids.clear();
    }

}
