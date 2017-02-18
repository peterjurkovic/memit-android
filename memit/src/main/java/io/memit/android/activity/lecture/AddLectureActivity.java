package io.memit.android.activity.lecture;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by peter on 2/18/17.
 */

public class AddLectureActivity extends BaseLectureActivity {


    AddLectureActivity(int titleId, int layoutId) {
        super(titleId, layoutId);
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
