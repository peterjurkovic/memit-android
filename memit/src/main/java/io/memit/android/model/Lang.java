package io.memit.android.model;

import java.util.ArrayList;
import java.util.List;

import io.memit.android.R;

/**
 * Created by peter on 2/4/17.
 */

public enum Lang implements SpinnerState {
    BG(R.string.lang_BG),
    ZH(R.string.lang_HR),
    HR(R.string.lang_HR),
    CS(R.string.lang_CS),
    DA(R.string.lang_DA),
    EO(R.string.lang_EO),
    FI(R.string.lang_FI),
    NL(R.string.lang_NL),
    FR(R.string.lang_FR),
    DE(R.string.lang_DE),
    EL(R.string.lang_EL),
    HU(R.string.lang_HU),
    ID(R.string.lang_ID),
    IT(R.string.lang_IT),
    NB(R.string.lang_NB),
    PL(R.string.lang_PL),
    PT(R.string.lang_PT),
    RO(R.string.lang_RO),
    RU(R.string.lang_RU),
    SK(R.string.lang_SK),
    SL(R.string.lang_SL),
    ES(R.string.lang_ES),
    SV(R.string.lang_SV),
    TR(R.string.lang_TR);

    private final int resource;

    private Lang(int resource) {
        this.resource = resource;
    }

    public String getId() {
        return this.toString();
    }

    public int getResource() {
        return resource;
    }

    public static List<SpinnerState> toList(){
        ArrayList<SpinnerState> list = new ArrayList<>(values().length);
        for(SpinnerState state : values()){
            list.add(state);
        }
        return list;
    }

}
