package io.memit.android.activity.word;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.memit.android.R;
import io.memit.android.activity.AbstractActivity;
import io.memit.android.model.Lang;
import io.memit.android.provider.Contract;
import io.memit.android.provider.Contract.Word;
import io.memit.android.tools.CursorUtils;
import io.memit.android.tools.UriUtils;

import static io.memit.android.tools.UriUtils.removeLastSegment;

/**
 * Created by peter on 3/5/17.
 */

public abstract class BaseWordActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    final static String BOOK_LECTURE_WORDS_URI_EXTRA = "bookLecturesWordsUri";
    final static byte LOAD_LECTURE_LOADER = 1;
    // books/{id}/lectures/{id}/words
    protected Uri bookLecturesWordsUri;

    protected final int layoutId;
    protected final int titleId;

    protected EditText questionInput;
    protected EditText answerInput;
    protected TextView header;

    protected Button saveButton;
    protected Button cancelButton;
    protected CoordinatorLayout root;

    private RelativeLayout form;
    private RelativeLayout loader;

    public BaseWordActivity(int titleId, int layoutId){
        this.layoutId = layoutId;
        this.titleId = titleId;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_dynamic);
        toolbar.setTitle(titleId);
        root = (CoordinatorLayout) findViewById(R.id.root);
        form = (RelativeLayout) root.findViewById(R.id.form);
        loader = (RelativeLayout) root.findViewById(R.id.loadingPanel);

        bookLecturesWordsUri = getIntent().getExtras().getParcelable(BOOK_LECTURE_WORDS_URI_EXTRA);
        initForm();
        if( ! isStandalonAction()){
            getLoaderManager().initLoader(LOAD_LECTURE_LOADER, null, this);
        }else {
            renderStandalonForm();
        }


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });
        useBackButtonIn(toolbar);


    }

    protected abstract void onSaveButtonClicked();

    protected void initForm(){
        if(questionInput == null)
            questionInput = (EditText) findViewById(R.id.wordQuestionInput);
        if(answerInput  == null)
            answerInput = (EditText) findViewById(R.id.wordAnswerInput);
        if(saveButton == null)
            saveButton = (Button) findViewById(R.id.wordSaveBtn);
        if(cancelButton == null)
            cancelButton= (Button) findViewById(R.id.wordCancelBtn);
        if(header == null)
            header = (TextView) findViewById(R.id.header);
    }

    protected void setQuestionInputHit(String lang){
        setHit(questionInput, R.string.word_add_question, lang);
    }

    protected void setAnswerInputHit(String lang){
        setHit(answerInput, R.string.word_add_answer, lang);
    }

    protected void setHeader(String header){
        this.header.setText(header);
    }


    protected void hideLoader(){
        loader.setVisibility(View.GONE);
        form.setVisibility(View.VISIBLE);
    }

    protected void setHit(EditText input, int res, String langCode){
        if(langCode != null){
            Lang lang = Lang.valueOf(langCode.toUpperCase());
            if(lang != null){
                String langName = getString(lang.getResource());
                String hit = getString(res, langName);
                input.setHint(hit);
            }
        }
    }

    protected boolean isStandalonAction(){
        return bookLecturesWordsUri == null;
    }

    protected void renderStandalonForm(){
        hideLoader();
    }

    protected boolean isFormValid(ContentValues cv){
        String question = cv.getAsString(Word.QUESTION);
        if(question.length() == 0){
            questionInput.setError(getString(R.string.word_required));
            return false;
        }
        String answer = cv.getAsString(Word.ANSWER);
        if(question.length() == 0){
            answerInput.setError(getString(R.string.word_required));
            return false;
        }
        return true;
    }

    protected ContentValues getConentValues(){
        String question = questionInput.getText().toString().trim();
        String answer = answerInput.getText().toString().trim();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Word.QUESTION, question);
        contentValues.put(Word.ANSWER, answer);
        contentValues.put(Word.LECTURE_ID, getLectureId());
        return contentValues;
    }


    protected Long getLectureId(){
        if(isStandalonAction()){
            return null;
        }
        return UriUtils.getLectureId(bookLecturesWordsUri);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOAD_LECTURE_LOADER){
            if(data != null && data.moveToFirst()){
                String langQuestion = CursorUtils.asString(data, Contract.Lecture.LANG_QUESTION);
                String langAnswer = CursorUtils.asString(data, Contract.Lecture.LANG_ANSWER);
                String lectureName = CursorUtils.asString(data, Contract.Lecture.NAME);
                setQuestionInputHit(langQuestion);
                setAnswerInputHit(langAnswer);
                setHeader(lectureName);
            }else{
                Log.e(getClass().getSimpleName(),"Could not load lecture information: " );
            }
        }
        hideLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(LOAD_LECTURE_LOADER == id){
            return new CursorLoader(this,
                    removeLastSegment(bookLecturesWordsUri),
                    null,
                    null,
                    null,
                    null);
        }
        return null;
    }



}
