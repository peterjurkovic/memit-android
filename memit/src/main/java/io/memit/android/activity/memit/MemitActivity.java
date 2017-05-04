package io.memit.android.activity.memit;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.memit.android.R;
import io.memit.android.activity.AbstractActivity;

/**
 * Created by peter on 4/23/17.
 */

public class MemitActivity extends AbstractActivity implements
        MemitSessionManager.OnSessionStartedListener,
        MemitSessionManager.OnNoMemoActivatedListener,
        MemitSessionManager.OnSessionEndedListener{

    private ProgressBar loader;
    private RelativeLayout contentLayout;
    private TextView cardsLeftView;
    private TextView questionView;
    private TextView seenView;
    private TextView questionSpeakerView;
    private EditText myAnswerView;
    private TextView myAnswerReadOnlyView;

    private Button showAnswerBtn;
    private RelativeLayout answerLayout;
    private TextView answerView;
    private TextView answerSpeakerView;

    private Memo memo;

    private MemitSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initDrawer(toolbar, savedInstanceState);
        initViews();
        sessionManager = new MemitSessionManager(this);
        sessionManager.sessionStart();
    }



    @Override
    public void onSessionStarted() {
        this.memo = sessionManager.getNext();
    }

    @Override
    public void onNoMemoActivated() {
        Log.i("Memig", "No cards activated");
    }

    @Override
    public void onSessionEnded(MemitSession session) {
        Log.i("Memig", "On session ended");
    }

    private void initViews(){
        loader = (ProgressBar) findViewById(R.id.loader);
        contentLayout = (RelativeLayout) findViewById(R.id.content);
        cardsLeftView =  (TextView) findViewById(R.id.cardsLeft);
        questionView =  (TextView) findViewById(R.id.question);
        questionSpeakerView = (TextView) findViewById(R.id.questionSpeaker);
        seenView =  (TextView) findViewById(R.id.seen);
        myAnswerView = (EditText) findViewById(R.id.myAnswer);
        myAnswerReadOnlyView  = (TextView) findViewById(R.id.myAnswerReadOnly);
        showAnswerBtn = (Button) findViewById(R.id.showAnswerBtn);
        answerLayout = (RelativeLayout) findViewById(R.id.answerLayout);
        answerSpeakerView = (TextView) findViewById(R.id.answerSpeaker);
    }


    private void setMemo(Memo memo){
        cardsLeftView.setText(memo.);
    }


}
