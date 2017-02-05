package io.memit.android.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import io.memit.android.R;

/**
 * Created by peter on 2/1/17.
 */

public class AddBookActivity extends BaseBookActivity {

    private static final String TAG =  AddBookActivity.class.getSimpleName();

    final Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_dynamic);
        toolbar.setTitle(R.string.book_add_new);
        setSupportActionBar(toolbar);
        prepareSpinners();
        initDrawer(toolbar,savedInstanceState);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,BookListActivity.class));
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBook();
            }
        });
    }

    private void saveBook(){
        final ContentValues contentValues = new ContentValues();
        String bookName = getBookName();
        String langOfQuestion = getLangOfQuestion();
        String langOfAnswer = getLangOfAnswer();

        Log.i(TAG, "bookName: " + bookName +" langOfQuestion:" + langOfQuestion +" langOfAnswer:"+langOfAnswer);
        //Snackbar.make(context, getString(R.string.device_saved_message, ContentUris.parseId(uri)), Snackbar.LENGTH_SHORT).show();
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
