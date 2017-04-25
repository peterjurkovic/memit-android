package io.memit.android.activity.drill;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import io.memit.android.BaseActivity;
import io.memit.android.R;
import io.memit.android.activity.AbstractActivity;

/**
 * Created by peter on 4/23/17.
 */

public class DrillActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drill);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initDrawer(toolbar, savedInstanceState);
    }
}
