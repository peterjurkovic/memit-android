package io.memit.android.activity.memit;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import io.memit.android.R;
import io.memit.android.activity.AbstractActivity;

/**
 * Created by peter on 4/23/17.
 */

public class MemitActivity extends AbstractActivity implements
        MemitSessionManager.OnSessionStartedListener,
        MemitSessionManager.OnNoMemoActivatedListener{

    private MemitSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initDrawer(toolbar, savedInstanceState);
        sessionManager = new MemitSessionManager(this);
        sessionManager.sessionStart();
    }



    @Override
    public void onSessionStarted() {
        MemitSession session = sessionManager.getSession();
        Log.i("Memig", session.toString());
    }

    @Override
    public void onNoMemoActivated() {
        Log.i("Memig", "No cards activated");
    }
}
