package io.memit.android.activity.word;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.ref.WeakReference;
import java.util.Collection;

import io.memit.android.BuildConfig;
import io.memit.android.R;
import io.memit.android.provider.Contract;
import io.memit.android.tools.HashSetMultiSelector;
import io.memit.android.tools.StringsUtils;

/**
 * Created by peter on 3/11/17.
 */

public class ModalMultiSelectorCallback implements ActionMode.Callback  {

    private final String tag = getClass().getName();

    private final byte DELETE = 1;
    private final byte ACTIVATE = 2;
    private final byte DEACTIVATE = 3;

    private final WeakReference<WordListActivity> activity;
    private final WeakReference<HashSetMultiSelector>  selector;
    private final QueryHelper queryHelper ;

    public ModalMultiSelectorCallback(WordListActivity context, HashSetMultiSelector selector){
        this.activity = new WeakReference<WordListActivity>(context);
        this.selector = new WeakReference<HashSetMultiSelector>( selector );
        this.queryHelper = new QueryHelper();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.word_action_mode_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if(selector.get() != null){
            Collection<String> ids = selector.get().getAllIds();
            if(ids.size() > 0)
                switch (item.getItemId()){
                    case R.id.activate:
                        activate(ids);
                        break;
                    case R.id.deactivate:
                        deactivate(ids);
                        break;
                    case R.id.delete:
                        update(ids, Contract.SyncColumns.DELETED, 1);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Unexpected item id: " + item.getItemId());
                }
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if( ! isActivityAlive() ){
            return;
        }
        activity.get().reloadView();
    }

    private void activate(Collection<String> ids){
        toggle(ids, 1);
    }

    private void deactivate(Collection<String> ids){
        toggle(ids, 0);
    }

    private void toggle(Collection<String> ids, int active){
        update(ids, Contract.Word.ACTIVE, active);
    }

    private void update(Collection<String> ids, String column, int val){
        String condition = StringsUtils.joinConditions(ids, "_id", "OR");
        ContentValues cv = new ContentValues();
        cv.put(column, val);
        queryHelper.startUpdate(ACTIVATE, null, activity.get().getUri(), cv, condition, null );
    }


    private boolean isActivityAlive(){
        WordListActivity context = activity.get();
        if(context == null || context.isFinishing()){
            return false;
        }
        return true;
    }

    private class QueryHelper extends AsyncQueryHandler {

        public QueryHelper() {
            super(activity.get().getContentResolver());
        }

        @Override
        public void startQuery(int token, Object cookie, Uri uri,
                                String[] projection, String selection, String[] selectionArgs, String orderBy) {

            if( isActivityAlive() ){
                activity.get().showLoader();
                super.startQuery(token, cookie, activity.get().getUri(),
                            projection, selection, selectionArgs, orderBy);
            }

        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            if( ! isActivityAlive()){
                return;
            }
            activity.get().reloadView();
        }

        @Override
        protected void onDeleteComplete(int position, Object cookie, int result) {
            Log.i(tag, "Number of deleted words: " + result);

            if( ! isActivityAlive() ){
                return;
            }
            activity.get().reloadView();
           // activity.getLoaderManager().restartLoader(LOADER_ID_BOOK, null, activity);
           // Snackbar.make(activity.root, R.string.book_removed, Snackbar.LENGTH_SHORT).show();
        }


    }
}
