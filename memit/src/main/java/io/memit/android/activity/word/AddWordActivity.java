package io.memit.android.activity.word;

import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

import io.memit.android.BaseActivity;
import io.memit.android.R;
import io.memit.android.activity.lecture.LectureListActivity;

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
        super.onLoadFinished(loader, data);
        hideLoader();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    protected void onSaveButtonClicked() {
        ContentValues cv = getConentValues();
        if(isFormValid(cv)){
            for(int i = 0; i < 250;i++ )
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
