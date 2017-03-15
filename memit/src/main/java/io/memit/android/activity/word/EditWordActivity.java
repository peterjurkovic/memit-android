package io.memit.android.activity.word;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import io.memit.android.R;

import static io.memit.android.tools.UriUtils.removeLastSegment;

/**
 * Created by peter on 3/9/17.
 */

public class EditWordActivity extends BaseWordActivity {

    private final static String TAG = EditWordActivity.class.getName();
    private final static byte LOAD_WORD_LOADER = 2;

    public EditWordActivity() {
        super(R.string.word_add_new, R.layout.activity_word_edit);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        hideLoader();
    }

    @Override
    protected void onSaveButtonClicked() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(LOAD_WORD_LOADER == id){
            return new CursorLoader(this,
                    removeLastSegment(bookLecturesWordsUri),
                    null,
                    null,
                    null,
                    null);
        }
        return super.onCreateLoader(id, args);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
