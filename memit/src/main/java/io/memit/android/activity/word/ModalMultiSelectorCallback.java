package io.memit.android.activity.word;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.Collection;

import io.memit.android.R;
import io.memit.android.provider.Contract;
import io.memit.android.tools.HashSetMultiSelector;
import io.memit.android.tools.StringsUtils;

import static io.memit.android.provider.Contract.SyncColumns.DELETED;

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
                        update(ids, DELETED, 1, DELETE);
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
        toggle(ids, 1, ACTIVATE);
    }

    private void deactivate(Collection<String> ids){
        toggle(ids, 0, DEACTIVATE);
    }

    private void toggle(Collection<String> ids, int active, byte action){
        update(ids, Contract.Word.ACTIVE, active, action);
    }

    private void update(Collection<String> ids, String column, int val, byte action){
        String condition = StringsUtils.joinConditions(ids, "_id", "OR");
        ContentValues cv = new ContentValues();
        cv.put(column, val);
        queryHelper.startUpdate(action, condition, activity.get().getUri(), cv, condition, null );
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
        protected void onUpdateComplete(int token, final Object condition, int result) {
            if( ! isActivityAlive()){
                return;
            }
            activity.get().reloadView();
            if(token > 0){

                if(token == ACTIVATE){
                    Snackbar.make(activity.get().getRecyclerView(), R.string.activated, Snackbar.LENGTH_SHORT);
                }else if(token == DEACTIVATE){
                    Snackbar.make(activity.get().getRecyclerView(), R.string.deactivated, Snackbar.LENGTH_SHORT);
                }else if(token == DELETE){
                    Snackbar.make(activity.get().getRecyclerView(), R.string.book_removed, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(condition instanceof  String){
                                        ContentValues cv = new ContentValues();
                                        cv.put(DELETED, 0);
                                        queryHelper
                                                .startUpdate(-1, condition, activity.get().getUri(), cv, (String)condition, null );
                                    }
                                }
                            }).show();
                }

            }
        }

        @Override
        protected void onDeleteComplete(int position, final Object condition, int result) {
            Log.i(tag, "Number of deleted words: " + result);

            if( ! isActivityAlive() ){
                return;
            }
            activity.get().reloadView();


        }


    }
}
