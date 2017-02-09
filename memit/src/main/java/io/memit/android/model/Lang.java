package io.memit.android.model;

import java.util.ArrayList;
import java.util.List;

import io.memit.android.R;

/**
 * Created by peter on 2/4/17.
 */

public enum Lang implements SpinnerState {
    BG(R.string.lang_BG, R.drawable.flag_bg),
    ZH(R.string.lang_ZH, R.drawable.flag_hz),
    HR(R.string.lang_HR, R.drawable.flag_hr),
    CS(R.string.lang_CS, R.drawable.flag_cs),
    DA(R.string.lang_DA, R.drawable.flag_da),
    FI(R.string.lang_FI, R.drawable.flag_fi),
    NL(R.string.lang_NL, R.drawable.flag_nl),
    EN(R.string.lang_EN, R.drawable.flag_en),
    FR(R.string.lang_FR, R.drawable.flag_fr),
    DE(R.string.lang_DE, R.drawable.flag_de),
    EL(R.string.lang_EL, R.drawable.flag_el),
    HU(R.string.lang_HU, R.drawable.flag_hu),
    IT(R.string.lang_IT, R.drawable.flag_it),
    NB(R.string.lang_NB, R.drawable.flag_nb),
    PL(R.string.lang_PL, R.drawable.flag_pl),
    PT(R.string.lang_PT, R.drawable.flag_pt),
    RO(R.string.lang_RO, R.drawable.flag_ro),
    RU(R.string.lang_RU, R.drawable.flag_ru),
    SK(R.string.lang_SK, R.drawable.flag_sk),
    SL(R.string.lang_SL, R.drawable.flag_sl),
    ES(R.string.lang_ES, R.drawable.flag_es),
    SV(R.string.lang_SV, R.drawable.flag_sv),
    TR(R.string.lang_TR, R.drawable.flag_tr);

    private final int resource;
    private final int drawable;

    private Lang(int resource, int drawable) {
        this.resource = resource;
        this.drawable = drawable;
    }

    public String getId() {
        return this.toString();
    }

    public int getResource() {
        return resource;
    }

    public int getDrawable(){
        return drawable;
    }

    public static List<SpinnerState> toList(){
        ArrayList<SpinnerState> list = new ArrayList<>(values().length);
        for(SpinnerState state : values()){
            list.add(state);
        }
        return list;
    }


}
