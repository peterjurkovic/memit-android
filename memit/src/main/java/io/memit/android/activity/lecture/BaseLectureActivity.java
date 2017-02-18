package io.memit.android.activity.lecture;

import android.app.LoaderManager;
import android.database.Cursor;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import io.memit.android.activity.AbstractActivity;

/**
 * Created by peter on 2/18/17.
 */

abstract class BaseLectureActivity  extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    protected final int layoutId;
    protected final int titleId;

    protected EditText bookNameEditText;
    protected Spinner questionSpinner;
    protected Spinner answerSpinner;
    protected Spinner levelSpinner;

    protected Button saveButton;
    protected Button cancelButton;

    BaseLectureActivity(int titleId, int layoutId){
        this.layoutId = layoutId;
        this.titleId = titleId;
    }

}
