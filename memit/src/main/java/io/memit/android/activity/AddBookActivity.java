package io.memit.android.activity;

import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import io.memit.android.R;
import io.memit.android.provider.Contract;

/**
 * Created by peter on 2/1/17.
 */

public class AddBookActivity extends BaseBookActivity {

    private static final String TAG =  AddBookActivity.class.getSimpleName();

    public AddBookActivity(){
        super(R.layout.activity_book_add, R.string.book_add_new);
    }


    @Override
    protected void onSaveButtonClicked() {
        ContentValues cv = getConentValues();
        if( isBookValid(cv) ){
            Uri uri = getContentResolver().insert(Contract.Book.CONTENT_URI, cv);
            goToBookDetail(uri.getLastPathSegment());
        }
    }

    @Override
    protected String bookId() {
        return null;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
