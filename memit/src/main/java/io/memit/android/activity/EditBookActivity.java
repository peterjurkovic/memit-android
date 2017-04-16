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
import android.util.Log;
import android.widget.Spinner;

import io.memit.android.R;
import io.memit.android.activity.lecture.LectureListActivity;
import io.memit.android.adapter.SpinnerAdapter;
import io.memit.android.provider.Contract;
import io.memit.android.provider.Contract.Book;
import io.memit.android.tools.CursorUtils;
import io.memit.android.tools.UriUtils;

/**
 * Created by peter on 2/8/17.
 */

public class EditBookActivity extends BaseBookActivity {

    private static final String TAG =  EditBookActivity.class.getSimpleName();

    private final static int ID_BOOK = 2;
    public final static String BOOK_EXTRA_URI = "bookUri";
    public final static String REFERER_EXTRA = "referer";
    private Uri bookIdUri;
    private String referer;


    public EditBookActivity(){
        super(R.layout.activity_book_edit, R.string.book_edit_existing);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookIdUri = getIntent().getExtras().getParcelable(BOOK_EXTRA_URI);
        referer = getIntent().getExtras().getString(REFERER_EXTRA);
        getLoaderManager().initLoader(ID_BOOK, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                bookIdUri,
                Contract.allBookColumns(),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            bookNameEditText.setText(CursorUtils.asString(data, Book.NAME));
            String langQuestion = CursorUtils.asString(data,Book.LANG_QUESTION);
            preSelect(questionSpinner, langQuestion);
            String langAnswer = data.getString(data
                    .getColumnIndexOrThrow(Book.LANG_ANSWER));
            preSelect(answerSpinner, langAnswer);
            String level = CursorUtils.asString(data, Book.LEVEL);
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
            getContentResolver().update(bookIdUri, cv, null, null);

            if(shouldGoToBookDetail()){
                goToBookDetail(bookId());
            }else{
                Intent i = new Intent(this, BookListActivity.class);
                i.putExtra(SHOW_SAVED_EXTRA, true );
                startActivity(i);
            }
        }
    }

    private boolean shouldGoToBookDetail(){
        return referer != null && referer.equals(LectureListActivity.class.getName());
    }


    private void preSelect(Spinner spinner, String id){
        int pos = ((SpinnerAdapter) spinner.getAdapter()).positionOf(id);
        Log.i(TAG, "Position of spinner: " + pos +  " id: " + id );
        if(pos != -1){
            spinner.setSelection(pos);
        }
    }

    public String bookId() {
        return UriUtils.parseId(bookIdUri);
    }


}
