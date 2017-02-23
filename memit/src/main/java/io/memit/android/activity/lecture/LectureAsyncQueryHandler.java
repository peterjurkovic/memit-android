package io.memit.android.activity.lecture;

import android.content.AsyncQueryHandler;
import android.net.Uri;
import android.support.design.widget.Snackbar;

import java.lang.ref.WeakReference;

import io.memit.android.R;

/**
 * Created by peter on 2/22/17.
 */

class LectureAsyncQueryHandler extends AsyncQueryHandler {

    private final static byte DELETE = 1;

    private final WeakReference<LectureListActivity> weekActivity;

    public LectureAsyncQueryHandler(LectureListActivity activity) {
        super(activity.getContentResolver());
        this.weekActivity = new WeakReference<>(activity);
    }


    public void removeLecture(Uri uri) {
        startDelete(DELETE, null, uri, null, null);
    }

    @Override
    protected void onDeleteComplete(int position, Object cookie, int result) {
        LectureListActivity activity = weekActivity.get();
        if(activity == null || activity.isFinishing()){
            return;
        }
        activity.refresList();
        Snackbar.make(activity.findViewById(R.id.root), R.string.book_removed, Snackbar.LENGTH_LONG).show();
    }
}

