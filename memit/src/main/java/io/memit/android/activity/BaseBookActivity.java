package io.memit.android.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import io.memit.android.R;
import io.memit.android.adapter.SpinnerAdapter;
import io.memit.android.model.Lang;
import io.memit.android.model.Level;
import io.memit.android.model.SpinnerState;
import io.memit.android.provider.BookContract;
import io.memit.android.provider.BookDatabaseOperations;

/**
 * Created by peter on 2/4/17.
 */

public abstract class BaseBookActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    protected static final byte MIN_BOOK_NAME_LENGTH = 5;

    protected final Context context = this;
    protected final int layout;
    protected final int title;

    protected EditText bookNameEditText;
    protected Spinner questionSpinner;
    protected Spinner answerSpinner;
    protected Spinner levelSpinner;

    protected Button saveButton;
    protected Button cancelButton;

    protected CoordinatorLayout root;

    protected BaseBookActivity(int layout, int title){
        this.layout = layout;
        this.title = title;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_dynamic);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        prepareSpinners();
        root = (CoordinatorLayout) findViewById(R.id.root);
        initDrawer(toolbar,savedInstanceState);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), BookListActivity.class));
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });

    }

    protected abstract  void onSaveButtonClicked();

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

    protected ContentValues getConentValues(){
        String bookName = getBookName().trim();
        String langOfQuestion = getLangOfQuestion().toLowerCase();
        String langOfAnswer = getLangOfAnswer().toLowerCase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(BookContract.Book.NAME, bookName);
        contentValues.put(BookContract.Book.LANG_QUESTION, langOfQuestion);
        contentValues.put(BookContract.Book.LANG_ANSWER, langOfAnswer);
        contentValues.put(BookContract.Book.LEVEL, getLevel());
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


    protected String getBookName(){
        Editable text = bookNameEditText.getEditableText();
        if(text == null){
            return null;
        }
        return text.toString();
    }


    protected boolean isBookValid(ContentValues cv){
        String bookName = cv.getAsString(BookContract.Book.NAME);
        if(bookName.length() < MIN_BOOK_NAME_LENGTH){
            bookNameEditText.setError( getString(R.string.book_form_name_short_error, MIN_BOOK_NAME_LENGTH ));
            return false;
        }
        boolean bookAlreadyExists = BookDatabaseOperations
                .getInstance(getApplicationContext())
                .exists(bookName, bookId());
        if(bookAlreadyExists){
            bookNameEditText.setError(getString(R.string.book_form_name_exists_error));
            return false;
        }
        return true;
    }

    protected abstract long bookId();

}
