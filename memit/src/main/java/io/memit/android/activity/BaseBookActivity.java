package io.memit.android.activity;

import android.app.LoaderManager;
import android.database.Cursor;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import io.memit.android.R;
import io.memit.android.adapter.SpinnerAdapter;
import io.memit.android.model.Lang;
import io.memit.android.model.Level;
import io.memit.android.model.SpinnerState;

/**
 * Created by peter on 2/4/17.
 */

public abstract class BaseBookActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    protected EditText bookNameEditText;
    protected Spinner questionSpinner;
    protected Spinner answerSpinner;
    protected Spinner levelSpinner;

    protected Button saveButton;
    protected Button cancelButton;


    protected void prepareSpinners(){
        initForm();
        List<SpinnerState> langs = Lang.toList();
        questionSpinner.setAdapter(new SpinnerAdapter(getApplicationContext(), langs));
        answerSpinner.setAdapter(new SpinnerAdapter(getApplicationContext(), langs));
       levelSpinner.setAdapter(new SpinnerAdapter(getApplicationContext(), Level.toList()));
    }

    protected void initForm(){
        if(questionSpinner == null)
            questionSpinner = (Spinner) findViewById(R.id.bookLangQuestionSpinner);
        if(answerSpinner  == null)
            answerSpinner = (Spinner) findViewById(R.id.bookLangAnswerSpinner);
        if(levelSpinner == null)
            levelSpinner = (Spinner)  findViewById(R.id.bookLevelSpinner);
        if(saveButton == null)
            saveButton = (Button) findViewById(R.id.bookSaveBtn);
        if(cancelButton== null)
            cancelButton= (Button) findViewById(R.id.bookCancelBtn);
        if(bookNameEditText == null){
            bookNameEditText = (EditText) findViewById(R.id.bookNameInput);
        }
    }

    protected String getLangOfQuestion(){
        return ((Lang) questionSpinner.getSelectedItem()).getId();
    }

    protected String getLangOfAnswer(){
        return ((Lang) answerSpinner.getSelectedItem()).getId();
    }


    protected String getBookName(){
        Editable text = bookNameEditText.getEditableText();
        if(text == null){
            return null;
        }
        return text.toString();
    }


}
