package io.memit.android.activity.word;

import android.content.AsyncQueryHandler;
import android.net.Uri;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.ref.WeakReference;
import java.util.Collection;

import io.memit.android.R;
import io.memit.android.tools.SparseArrayMultiSelector;
import io.memit.android.tools.StringsUtils;

/**
 * Created by peter on 3/11/17.
 */

public class ModalMultiSelectorCallback implements ActionMode.Callback  {

    private final byte DELETE = 1;
    private final byte ACTIVATE = 2;
    private final byte DEACTIVATE = 3;

    private final WeakReference<WordListActivity> activity;
    private final WeakReference<SparseArrayMultiSelector>  selector;
    private QueryHelper queryHelper;
    public ModalMultiSelectorCallback(WordListActivity context, SparseArrayMultiSelector selector){
        this.activity = new WeakReference<WordListActivity>(context);
        this.selector = new WeakReference<SparseArrayMultiSelector>( selector );
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
            Collection<Integer> ids = selector.get().getAllIds();
            if(ids.size() > 0)
                switch (item.getItemId()){
                    case R.id.activate:

                        break;

                    case R.id.deactivate:

                        break;

                    case R.id.delete:

                        break;
                }
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    private void activate(Collection<Object> ids){
        String condition = StringsUtils.joinConditions(ids, "_id", "OR");
        //  (int token, Object cookie, Uri uri, String selection, String[] selectionArgs) {
       // queryHelper.startUpdate(ACTIVATE, );
    }


    private class QueryHelper extends AsyncQueryHandler {

        public QueryHelper() {
            super(activity.get().getContentResolver());
        }

        @Override
        public void startQuery(int token, Object cookie, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {

            if( isActivityAlive() ){
                activity.get().showLoader();
                super.startQuery(token, cookie, activity.get().getUri(), projection, selection, selectionArgs, orderBy);
            }

        }

        @Override
        protected void onDeleteComplete(int position, Object cookie, int result) {
            if( ! isActivityAlive() ){
                return;
            }
            activity.get().hideLoader();
           // activity.getLoaderManager().restartLoader(LOADER_ID_BOOK, null, activity);
           // Snackbar.make(activity.root, R.string.book_removed, Snackbar.LENGTH_SHORT).show();
        }

        private boolean isActivityAlive(){
            WordListActivity context = activity.get();
            if(context == null || context.isFinishing()){
                return false;
            }
            return true;
        }

    }
}
