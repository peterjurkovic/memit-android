package io.memit.android.activity.lecture;

import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;

import io.memit.android.R;
import io.memit.android.provider.Contract;

/**
 * Created by peter on 2/18/17.
 */

public class AddLectureActivity extends BaseLectureActivity {

    private final static String TAG = AddLectureActivity.class.getName();

    public AddLectureActivity() {
        super(R.string.lecture_add_new, R.layout.activity_lecture_add);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(loader.getId() == BOOK_LOADER_ID){
            hideLoader();
            if(data != null && data.moveToFirst()) {
                String langQuestion = data.getString(data
                        .getColumnIndexOrThrow(Contract.Book.LANG_QUESTION));
                preSelect(questionSpinner, langQuestion);
                String langAnswer = data.getString(data
                        .getColumnIndexOrThrow(Contract.Book.LANG_ANSWER));

                preSelect(answerSpinner, langAnswer);
                String bookName = data.getString(data
                        .getColumnIndexOrThrow(Contract.Book.NAME));
                bookNameView.setText(bookName);
            }else{
                Log.w(TAG, "Can not load book [id="+getBookId()+"]");
                // TODO show some alert message
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onSaveButtonClicked() {
        ContentValues cv = getConentValues();
        if( isValid(cv) ){
            getContentResolver().insert(bookLecturesUri, cv);
            Snackbar.make(root, getString(R.string.book_saved), Snackbar.LENGTH_SHORT).show();
            goToLectureList();
        }
    }
}
