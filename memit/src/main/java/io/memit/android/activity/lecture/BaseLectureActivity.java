package io.memit.android.activity.lecture;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import io.memit.android.R;
import io.memit.android.activity.AbstractActivity;
import io.memit.android.adapter.SpinnerAdapter;
import io.memit.android.model.Lang;
import io.memit.android.model.Level;
import io.memit.android.model.SpinnerState;
import io.memit.android.provider.Contract;
import io.memit.android.provider.Contract.Lecture;
import io.memit.android.tools.UriUtils;

/**
 * Created by peter on 2/18/17.
 */

abstract class BaseLectureActivity  extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    protected final static int MIN_LECTURE_NAME = 1;
    protected final static int BOOK_LOADER_ID = 1;

    protected final int layoutId;
    protected final int titleId;

    protected EditText lectureNameEditText;
    protected Spinner questionSpinner;
    protected Spinner answerSpinner;
    protected Spinner levelSpinner;

    protected Button saveButton;
    protected Button cancelButton;
    protected CoordinatorLayout root;
    protected Uri bookLecturesUri;
    protected TextView bookNameView;

    private RelativeLayout form;
    private RelativeLayout loader;


    BaseLectureActivity(int titleId, int layoutId){
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
        prepareForm();

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
        bookLecturesUri = getIntent().getExtras().getParcelable(LectureListActivity.BOOK_LECTURES_URI_EXTRA);
        getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
    }


    protected abstract  void onSaveButtonClicked();

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursor = null;
        if(id == BOOK_LOADER_ID){
            cursor = new CursorLoader(this,
                    UriUtils.removeLastSegment(bookLecturesUri),
                    Contract.allBookColumns(),
                    null,
                    null,
                    null);
        }
        return cursor;
    }

    protected void prepareForm(){
        initForm();
        List<SpinnerState> langs = Lang.toList();
        questionSpinner.setAdapter(new SpinnerAdapter(this, langs));
        answerSpinner.setAdapter(new SpinnerAdapter(this, langs));
    }

    protected void initForm(){
        if(questionSpinner == null)
            questionSpinner = (Spinner) findViewById(R.id.lectureLangQuestionSpinner);
        if(answerSpinner  == null)
            answerSpinner = (Spinner) findViewById(R.id.lectureLangAnswerSpinner);
        if(saveButton == null)
            saveButton = (Button) findViewById(R.id.lectureSaveBtn);
        if(cancelButton== null)
            cancelButton= (Button) findViewById(R.id.lectureCancelBtn);
        if(lectureNameEditText == null)
            lectureNameEditText = (EditText) findViewById(R.id.lectureNameInput);
        if(bookNameView == null)
            bookNameView = (TextView) findViewById(R.id.lectureBookName);

    }


    protected ContentValues getConentValues(){
        String lectureName = getLectureName().trim();
        String langOfQuestion = getLangOfQuestion().toLowerCase();
        String langOfAnswer = getLangOfAnswer().toLowerCase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Lecture.NAME, lectureName);
        contentValues.put(Lecture.LANG_QUESTION, langOfQuestion);
        contentValues.put(Lecture.LANG_ANSWER, langOfAnswer);
        contentValues.put(Lecture.BOOK_ID, getBookId());
        return contentValues;
    }

    protected String getLangOfQuestion(){
        return ((Lang) questionSpinner.getSelectedItem()).getId();
    }

    protected String getLangOfAnswer(){
        return ((Lang) answerSpinner.getSelectedItem()).getId();
    }

    protected String getLevel(){
        return ((Level) levelSpinner.getSelectedItem()).getId();
    }


    protected String getLectureName(){
        Editable text = lectureNameEditText.getEditableText();
        if(text == null){
            return null;
        }
        return text.toString();
    }


    protected boolean isValid(ContentValues cv){
        String lectureName = cv.getAsString(Contract.Lecture.NAME);
        if(lectureName.length() < MIN_LECTURE_NAME){
            lectureNameEditText.setError( getString(R.string.error_string_short, MIN_LECTURE_NAME ));
            return false;
        }
        return true;
    }

    protected void preSelect(Spinner spinner, String id){
        int pos = ((SpinnerAdapter) spinner.getAdapter()).positionOf(id);
        if(pos != -1){
            spinner.setSelection(pos);
        }
    }

    protected void hideLoader(){
        loader.setVisibility(View.GONE);
        form.setVisibility(View.VISIBLE);
    }

    protected void goToLectureList(){
        Intent i = new Intent(this, LectureListActivity.class);
        i.putExtra(LectureListActivity.BOOK_LECTURES_URI_EXTRA, bookLecturesUri);
        i.putExtra(LectureListActivity.SHOW_SAVED_EXTRA, true);
        startActivity(i);
    }

    protected long getBookId(){
        return UriUtils.getBookId(bookLecturesUri);
    }



}
