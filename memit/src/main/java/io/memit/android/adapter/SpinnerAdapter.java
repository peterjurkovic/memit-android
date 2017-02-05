package io.memit.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.memit.android.R;
import io.memit.android.model.SpinnerState;

/**
 * Created by peter on 2/4/17.
 */

public class SpinnerAdapter extends ArrayAdapter<SpinnerState> {

    public SpinnerAdapter(Context context, List<SpinnerState> elements) {
        super(context, R.layout.spinner);
        setDropDownViewResource(R.layout.spinner_dropdown);
        addAll(elements);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);

        SpinnerState element = getItem(position);
        label.setText(element.getResource());
        // label.setCompoundDrawablesWithIntrinsicBounds(0, 0, element.getResource(), 0);

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);

        SpinnerState element = getItem(position);
        label.setText(element.getResource());
        //label.setCompoundDrawablesWithIntrinsicBounds(0, 0, element.get. getImageResourceId(), 0);

        return label;
    }
}
