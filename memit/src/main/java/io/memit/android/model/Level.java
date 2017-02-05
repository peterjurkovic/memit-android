package io.memit.android.model;

import java.util.ArrayList;
import java.util.List;

import io.memit.android.R;

/**
 * Created by peter on 2/1/17.
 */

public enum Level implements SpinnerState{
    A1(R.string.level_A1),
    A2(R.string.level_A2),
    B1(R.string.level_B1),
    B2(R.string.level_B2),
    C1(R.string.level_C1),
    C2(R.string.level_C2);

    private final int resource;

    @Override
    public int getResource() {
        return resource;
    }

    private Level(int resource){
        this.resource = resource;
    }

    public String getId(){
        return this.toString();
    }

    public static List<SpinnerState> toList(){
        ArrayList<SpinnerState> list = new ArrayList<>(values().length);
        for(SpinnerState state : values()){
            list.add(state);
        }
        return list;
    }
}
