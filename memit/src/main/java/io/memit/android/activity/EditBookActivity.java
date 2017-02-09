package io.memit.android.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Spinner;

import io.memit.android.R;
import io.memit.android.adapter.SpinnerAdapter;
import io.memit.android.provider.BookContract;

/**
 * Created by peter on 2/8/17.
 */

public class EditBookActivity extends BaseBookActivity {

    private static final String TAG =  EditBookActivity.class.getSimpleName();

    private final static int ID_BOOK = 2;
    public final static String BOOK_EXTRA_URI = "bookUri";
    private Uri bookUri;


    public EditBookActivity(){
        super(R.layout.activity_book_edit, R.string.book_edit_existing);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookUri = getIntent().getExtras().getParcelable(BOOK_EXTRA_URI);
        getLoaderManager().initLoader(ID_BOOK, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                bookUri,
                BookContract.allColumns(),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            bookNameEditText.setText(data.getString(data
                    .getColumnIndexOrThrow(BookContract.Book.NAME)));
            String langQuestion = data.getString(data
                    .getColumnIndexOrThrow(BookContract.Book.LANG_QUESTION));
            preSelect(questionSpinner, langQuestion);
            String langAnswer = data.getString(data
                    .getColumnIndexOrThrow(BookContract.Book.LANG_ANSWER));
            preSelect(answerSpinner, langAnswer);
            String level = data.getString(data
                    .getColumnIndexOrThrow(BookContract.Book.LEVEL));
            preSelect(levelSpinner, level);

        }else{
            Log.w(TAG, "Book not found. " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onSaveButtonClicked() {
        ContentValues cv = getConentValues();
        if( isBookValid(cv) ){
            getContentResolver().update(bookUri, cv, null, null);
            Snackbar.make(root, getString(R.string.book_saved), Snackbar.LENGTH_SHORT).show();
            Intent i = new Intent(context, BookListActivity.class);
            startActivity(i);
        }
    }

    private void preSelect(Spinner spinner, String id){
        int pos = ((SpinnerAdapter) spinner.getAdapter()).positionOf(id);
        Log.i(TAG, "Position of spinner: " + pos +  " id: " + id );
        if(pos != -1){
            spinner.setSelection(pos);
        }
    }

    public long bookId() {
        return ContentUris.parseId(bookUri);
    }
}
