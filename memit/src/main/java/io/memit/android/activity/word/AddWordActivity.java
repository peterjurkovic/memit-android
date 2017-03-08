package io.memit.android.activity.word;

import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;

import io.memit.android.BaseActivity;
import io.memit.android.R;
import io.memit.android.activity.lecture.LectureListActivity;
import io.memit.android.provider.Contract.Lecture;
import io.memit.android.tools.CursorUtils;

/**
 * Created by peter on 3/5/17.
 */

public class AddWordActivity extends BaseWordActivity {

    final static String TAG = AddWordActivity.class.getSimpleName();

    public AddWordActivity(){
        super(R.string.word_add_new, R.layout.activity_word_add);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOAD_LECTURE_LOADER){
            if(data != null && data.moveToFirst()){
                String langQuestion = CursorUtils.asString(data, Lecture.LANG_QUESTION);
                String langAnswer = CursorUtils.asString(data, Lecture.LANG_ANSWER);
                String lectureName = CursorUtils.asString(data, Lecture.NAME);
                setQuestionInputHit(langQuestion);
                setAnswerInputHit(langAnswer);
                setHeader(lectureName);
            }else{
                Log.e(TAG,"Could not load lecture information: " );
            }
        }
        hideLoader();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    protected void onSaveButtonClicked() {
        ContentValues cv = getConentValues();
        if(isFormValid(cv)){
            for(int i = 0; i < 100; i++)
                getContentResolver().insert(bookLecturesWordsUri, cv);
            goBack();
        }
    }

    private void goBack(){
        Intent i;
        if(isStandalonAction()){
            i = new Intent(this, BaseActivity.class);
        }else{
            i = new Intent(this, WordListActivity.class);
            i.putExtra(WordListActivity.BOOK_LECTURES_URI_EXTRA, bookLecturesWordsUri);
        }
        i.putExtra(LectureListActivity.SHOW_SAVED_EXTRA, true);
        startActivity(i);

    }

}
