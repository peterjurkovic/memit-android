package io.memit.android.activity.memit;

import android.content.res.Resources;
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
        MemitSessionManager.OnSessionCreatedListener,
        MemitSessionManager.OnNoMemoActivatedListener,
        MemitSessionManager.OnSessionEndedListener,
        MemitSessionManager.OnSessionNotStartedListener{

    private final static String TAG = MemitActivity.class.getSimpleName();

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
    public void onSessionCreated(MemitSession session) {
        Log.i(TAG, "onSessionCreated: " + session.toString());
        setCardsLeft(session.getActivatedCount());
        this.memo = sessionManager.getNext();
        updateMemo();
    }

    @Override
    public void onSessionNotStarted() {

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
        answerView = (TextView) findViewById(R.id.answer);
        answerSpeakerView = (TextView) findViewById(R.id.answerSpeaker);

    }


    private void updateMemo(){
        if(memo != null){
            questionView.setText(memo.question);
            answerView.setText(memo.answer);
            seenView.setText(getResources().getString(R.string.seen, memo.hits));
        }
    }

    public void setCardsLeft(int value){
        Resources res = getResources();
        cardsLeftView.setText(res.getQuantityString(R.plurals.cardsLeft, value, value));
    }



}
