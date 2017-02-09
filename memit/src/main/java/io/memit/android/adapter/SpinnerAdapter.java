package io.memit.android.adapter;

import android.content.Context;
import android.util.Log;
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
        if(element.getDrawable() != -1)
            label.setCompoundDrawablesWithIntrinsicBounds(element.getDrawable(), 0, 0, 0);
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);

        SpinnerState element = getItem(position);
        label.setText(element.getResource());
        if(element.getDrawable() != -1)
            label.setCompoundDrawablesWithIntrinsicBounds(element.getDrawable(), 0, 0, 0);

        return label;
    }

    public int positionOf(String id){
        for(int i = 0; i < getCount(); i++){
            SpinnerState item = getItem(i);
            Log.d("Spinner", item + " is at pos " + i);
            if(item != null && item.getId().equalsIgnoreCase(id)){
                return i;
            }
        }
        return -1;
    }

}
