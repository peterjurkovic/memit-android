package io.memit.android.activity.memit;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.memit.android.R;
import io.memit.android.activity.AbstractActivity;
import io.memit.android.model.Lang;

/**
 * Created by peter on 4/23/17.
 */

public class MemitActivity extends AbstractActivity implements
        MemitSessionManager.OnSessionCreatedListener,
        MemitSessionManager.OnNoMemoActivatedListener,
        MemitSessionManager.OnSessionEndedListener,
        MemitSessionManager.OnSessionNotStartedListener,
        MemitSessionManager.OnNumberOfActiveCardsChangedListener{

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
        showAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRateButtons();
            }
        });
        sessionManager = new MemitSessionManager(this);
        sessionManager.sessionStart();
    }


    @Override
    public void onSessionCreated(MemitSession session) {
        loader.setVisibility(View.GONE);
        Log.i(TAG, "onSessionCreated: " + session.toString());
        this.memo = sessionManager.getNext();
        refresh();
        TransitionManager.beginDelayedTransition(contentLayout);
        contentLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSessionNotStarted() {
        loader.setVisibility(View.GONE);
    }

    @Override
    public void onNoMemoActivated() {
        Log.i("Memig", "No cards activated");

    }

    @Override
    public void onSessionEnded(MemitSession session) {
        Log.i("Memig", "On session ended");
    }

    public void onRateClicked(View view){
        if(memo != null){
            Rating rating = Rating.valueOf((String)view.getTag());
            Log.i(TAG, rating.toString());
            this.memo = sessionManager.rateAndGetNext(memo, rating);
            refresh();
        }else{
            Log.i(TAG, "onRateClicked bug the memo is null");
        }
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


    private void refresh(){
        if(memo != null){
            questionView.setText(memo.question);
            answerView.setText(memo.answer);
            seenView.setText(getResources().getString(R.string.seen, memo.hits));
            setLang(memo.questionLang, questionSpeakerView);
            setLang(memo.answerLang, answerSpeakerView);
            answerLayout.setVisibility(View.GONE);
            myAnswerView.setVisibility(View.VISIBLE);
            myAnswerView.setText(null);
            myAnswerReadOnlyView.setVisibility(View.GONE);
            showAnswerBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setLang(Lang lang, TextView view){
        if(lang != null){
            view.setText(getResources().getString(lang.getResource()).toUpperCase());
            view.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.GONE);
        }
    }

    public void showRateButtons(){
        showAnswerBtn.setVisibility(View.GONE);
        myAnswerView.setVisibility(View.GONE);
        if(myAnswerView.getText().length() > 0){
            myAnswerReadOnlyView.setText(myAnswerView.getText());
            myAnswerReadOnlyView.setVisibility(View.VISIBLE);
        }
        TransitionManager.beginDelayedTransition(answerLayout);
        answerLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onNumberOfActiveCardsChanged(int currentNumber) {
        Resources res = getResources();
        cardsLeftView.setText(res.getQuantityString(R.plurals.cardsLeft, currentNumber, currentNumber));
    }
}
