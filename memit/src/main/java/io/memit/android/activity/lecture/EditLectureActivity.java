package io.memit.android.activity.lecture;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import io.memit.android.R;
import io.memit.android.provider.Contract;
import io.memit.android.provider.Contract.Book;
import io.memit.android.provider.Contract.Lecture;

/**
 * Created by peter on 2/20/17.
 */

public class EditLectureActivity extends BaseLectureActivity{

    public final static String BOOK_LECTURE_ID_URI_EXTRA = "bookLectureId";
    private final static String TAG = EditLectureActivity.class.getName();
    private final static byte LOAD_LECTURE_LOADER = 3;
    private AtomicInteger counter = new AtomicInteger(2);

    public EditLectureActivity(){
        super(R.string.lecture_edit, R.layout.activity_lecture_edit);
    }

    private Uri bookLectureIdUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookLectureIdUri = getIntent().getExtras().getParcelable(BOOK_LECTURE_ID_URI_EXTRA);
        getLoaderManager().initLoader(LOAD_LECTURE_LOADER, null, this);
    }

    @Override
    protected void onSaveButtonClicked() {
        ContentValues cv = getConentValues();
        if( isValid(cv) ){
            getContentResolver().update(bookLectureIdUri, cv, null, null);
            goToLectureList();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        Loader<Cursor> loader = super.onCreateLoader(id, args);
        if(loader != null){
            return loader;
        }
        switch (id) {
            case LOAD_LECTURE_LOADER:
                return new CursorLoader(this,
                        bookLecturesUri,
                        Contract.allLectureColumns(),
                        null,
                        null,
                        null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(loader.getId() == BOOK_LOADER_ID){

            if(data != null && data.moveToFirst()) {
                String bookName = data.getString(data.getColumnIndexOrThrow(Book.NAME));
                bookNameView.setText(bookName);
            }else{
                Log.w(TAG, "Can not load book [id="+getBookId()+"]");
                // TODO show some alert message
            }
        }else if(loader.getId() == LOAD_LECTURE_LOADER){
            if(data != null && data.moveToNext()){
                String langQuestion = data.getString(data
                        .getColumnIndexOrThrow(Lecture.LANG_QUESTION));
                preSelect(questionSpinner, langQuestion);
                String langAnswer = data.getString(data
                        .getColumnIndexOrThrow(Lecture.LANG_ANSWER));
                preSelect(answerSpinner, langAnswer);
                String lectureName = data.getString(data.getColumnIndexOrThrow(Lecture.NAME));
                lectureNameEditText.setText(lectureName);
            }
        }
        if(counter.decrementAndGet() == 0){
            hideLoader();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
