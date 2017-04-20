package io.memit.android.activity.word;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.lang.ref.WeakReference;

import io.memit.android.R;
import io.memit.android.provider.Contract.Word;
import io.memit.android.tools.UriUtils;

import static io.memit.android.provider.Contract.SyncColumns.DELETED;

/**
 * Created by peter on 4/21/17.
 */

public class WordListAsyncQueryHelper extends AsyncQueryHandler {

    private final byte DELETE_ALL = 1;

    private final WeakReference<WordListActivity> activity;

    public WordListAsyncQueryHelper(WordListActivity activity) {
        super(activity.getContentResolver());
        this.activity = new WeakReference<WordListActivity>(activity);
    }


    public void removeAllInLecture(){
        String lectureId = UriUtils.getLectureId( activity.get().getUri() );
        activity.get().showLoader();
        String condition = new StringBuilder(Word.LECTURE_ID )
                            .append("=").append("'").append(lectureId).append("'").toString();
        super.startDelete(DELETE_ALL, condition, activity.get().getUri(), condition, null);
    }


    @Override
    protected void onDeleteComplete(int token, final Object cookie, int result) {
        if(!isActivityAlive()){
            return;
        }
        activity.get().reloadView();
        if(token == DELETE_ALL){
            Snackbar.make(activity.get().getRecyclerView(), R.string.deletedAll, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(cookie instanceof  String){
                                ContentValues cv = new ContentValues();
                                cv.put(DELETED, 0);
                                startUpdate(-1, null, activity.get().getUri(), cv, (String)cookie, null );
                            }
                        }
                    }).show();
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        if(!isActivityAlive()){
            return;
        }
        activity.get().reloadView();
    }

    private boolean isActivityAlive(){
        WordListActivity context = activity.get();
        if(context == null || context.isFinishing()){
            return false;
        }
        return true;
    }

}
