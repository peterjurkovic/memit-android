package io.memit.android.activity.lecture;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import io.memit.android.R;
import io.memit.android.provider.Contract;

/**
 * Created by peter on 2/20/17.
 */

public class EditLectureActivity extends BaseLectureActivity{

    private final static String TAG = EditLectureActivity.class.getName();

    private AtomicInteger counter = new AtomicInteger(2);

    public EditLectureActivity(){
        super(R.string.lecture_edit, R.layout.activity_lecture_edit);
    }

    @Override
    protected void onSaveButtonClicked() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == BOOK_LOADER_ID){
            counter.decrementAndGet();
            hideLoader();
            if(data != null && data.moveToFirst()) {
                String bookName = data.getString(data.getColumnIndexOrThrow(Contract.Book.NAME));
                bookNameView.setText(bookName);
            }else{
                Log.w(TAG, "Can not load book [id="+getBookId()+"]");
                // TODO show some alert message
            }
        }
        if(counter.get() == 0){
            hideLoader();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
