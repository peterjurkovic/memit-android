package io.memit.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import io.memit.android.R;
import io.memit.android.model.Lang;

/**
 * Created by peter on 2/4/17.
 */

public class LangAdapter extends ArrayAdapter<Lang> {

    public LangAdapter(Context context) {
        super(context, R.layout.spinner);
        setDropDownViewResource(R.layout.spinner_dropdown);
        addAll(Lang.values());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);

        Lang lang = getItem(position);
        label.setText(lang.getResource());
        // label.setCompoundDrawablesWithIntrinsicBounds(0, 0, lang.getResource(), 0);

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);

        Lang lang = getItem(position);
        label.setText(lang.getResource());
        //label.setCompoundDrawablesWithIntrinsicBounds(0, 0, lang.get. getImageResourceId(), 0);

        return label;
    }
}
