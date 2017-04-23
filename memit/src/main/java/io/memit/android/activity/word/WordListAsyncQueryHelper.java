package io.memit.android.activity.word;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.lang.ref.WeakReference;

import io.memit.android.R;
import io.memit.android.provider.Contract;
import io.memit.android.provider.Contract.Word;
import io.memit.android.tools.UriUtils;

import static io.memit.android.provider.Contract.SyncColumns.DELETED;

/**
 * Created by peter on 4/21/17.
 */

public class WordListAsyncQueryHelper extends AsyncQueryHandler {

    private final byte DELETE_ALL = 1;
    private final byte ACTIVATE_ALL = 2;
    private final byte DEACTIVATE_ALL = 3;

    private final WeakReference<WordListActivity> activity;

    public WordListAsyncQueryHelper(WordListActivity activity) {
        super(activity.getContentResolver());
        this.activity = new WeakReference<>(activity);
    }


    public void removeAllInLecture(){
        String condition = buildCondition();
        super.startDelete(DELETE_ALL, condition, activity.get().getUri(), condition, null);
    }

    public void activateAll(){
        setState(ACTIVATE_ALL, 1);
    }

    public void deactivateAll(){
        setState(DEACTIVATE_ALL, 0);
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
        int message = token == ACTIVATE_ALL ? R.string.activated : R.string.deactivated;
        Snackbar.make(activity.get().getRecyclerView(), message, Snackbar.LENGTH_SHORT).show();
    }

    private boolean isActivityAlive(){
        WordListActivity context = activity.get();
        if(context == null || context.isFinishing()){
            return false;
        }
        return true;
    }

    @NonNull
    private String buildCondition() {
        String lectureId = UriUtils.getLectureId( activity.get().getUri() );
        activity.get().showLoader();
        return new StringBuilder(Word.LECTURE_ID )
                .append("=").append("'").append(lectureId).append("'").toString();
    }

    private void setState(int token, int activate){
        String where = buildCondition();
        ContentValues cv = new ContentValues();
        cv.put(Word.ACTIVE, activate);
        super.startUpdate(token, null, activity.get().getUri(), cv, where, null);
    }



}
