package io.memit.android.activity.word;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import io.memit.android.BaseActivity;
import io.memit.android.R;
import io.memit.android.activity.lecture.LectureListActivity;
import io.memit.android.provider.Contract.Word;
import io.memit.android.tools.CursorUtils;
import io.memit.android.tools.UriUtils;

import static io.memit.android.tools.CursorUtils.asString;

/**
 * Created by peter on 3/9/17.
 */

public class EditWordActivity extends BaseWordActivity {

    // /books/{id}/lectures/{id}/words/{id}
    final static String BOOK_LECTURE_WORD_ID_EXTRA = "bookLectureWord";
    private final static byte LOAD_WORD_LOADER = 2;

    private Uri bookLecturesWordsIdUri;

    public EditWordActivity() {
        super(R.string.word_edit_new, R.layout.activity_word_edit);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookLecturesWordsIdUri = getIntent().getExtras().getParcelable(BOOK_LECTURE_WORD_ID_EXTRA);
        getLoaderManager().initLoader(LOAD_WORD_LOADER, null, this);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("EditWord", "onLoadFinished: " + loader.getId());
        if(loader.getId() == LOAD_WORD_LOADER){
            if(data != null && data.moveToFirst()){
                String question = asString(data, Word.QUESTION);
                String answer = asString(data, Word.ANSWER);
                questionInput.setText(question);
                answerInput.setText(answer);
            }else{
                Log.e(getClass().getSimpleName(),"Could not load word information: " );
            }
            return;
        }
        super.onLoadFinished(loader, data);
        hideLoader();
    }

    @Override
    protected void onSaveButtonClicked() {
        ContentValues cv = getConentValues();
        if(isFormValid(cv)){
            getContentResolver().update(bookLecturesWordsIdUri,
                    cv,
                    null,
                    null);
            goBack();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(LOAD_WORD_LOADER == id){
             return new CursorLoader(this,
                    bookLecturesWordsIdUri,
                    new String[]{Word.QUESTION, Word.ANSWER},
                    Word._ID + "= ? " ,
                    new String[]{UriUtils.getWordId(bookLecturesWordsIdUri)},
                    null);
        }
        return super.onCreateLoader(id, args);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
