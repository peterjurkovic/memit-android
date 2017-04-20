package io.memit.android.activity.word;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.memit.android.R;
import io.memit.android.activity.AbstractActivity;
import io.memit.android.activity.lecture.EditLectureActivity;
import io.memit.android.activity.lecture.LectureListActivity;
import io.memit.android.provider.Contract;
import io.memit.android.provider.Contract.Lecture;
import io.memit.android.provider.Contract.Word;
import io.memit.android.tools.HashSetMultiSelector;
import io.memit.android.tools.UriUtils;

import static io.memit.android.activity.lecture.EditLectureActivity.BOOK_LECTURE_ID_URI_EXTRA;
import static io.memit.android.activity.word.BaseWordActivity.BOOK_LECTURE_WORDS_URI_EXTRA;
import static io.memit.android.activity.word.EditWordActivity.BOOK_LECTURE_WORD_ID_EXTRA;
import static io.memit.android.tools.CursorUtils.asBool;
import static io.memit.android.tools.CursorUtils.asString;
import static io.memit.android.tools.UriUtils.getLectureIdAsString;
import static io.memit.android.tools.UriUtils.removeLastSegment;
import static io.memit.android.tools.UriUtils.removeTwoLastSegment;
import static io.memit.android.tools.UriUtils.withAppendedId;

/**
 * Created by peter on 2/23/17.
 */

public class WordListActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG =  WordListActivity.class.getSimpleName();
    private static final byte WORDS_LOADER = 1;
    private static final byte LECTURE_LOADER = 2;
    private static final String SELECTION_POSITIONS = "position";
    public  static final String BOOK_LECTURES_URI_EXTRA = "booklecturesWordsUri";

    private Colors colors;
    private RecyclerView recyclerView;
    private TextView empty;
    private ProgressBar loader;
    // books/{id}/lectures/{id}/words
    private Uri bookLecturesWordsUri;


    private final HashSetMultiSelector multiSelector = new HashSetMultiSelector() ;
    ModalMultiSelectorCallback multiSelectorCallback;
    ActionMode actionMode;
    private WordListCursorAdapter adatper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.word_toolbar);
        bookLecturesWordsUri = getIntent().getExtras().getParcelable(BOOK_LECTURES_URI_EXTRA);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        useBackButtonIn(toolbar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WordListActivity.this, LectureListActivity.class);
                i.putExtra(LectureListActivity.BOOK_LECTURES_URI_EXTRA, removeTwoLastSegment(bookLecturesWordsUri));
                startActivity(i);
            }
        });
        adatper = new WordListCursorAdapter();
        loader = (ProgressBar) findViewById(R.id.loader);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adatper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        empty = (TextView) findViewById(R.id.empty);

        appBarLayout.addOnOffsetChangedListener(new ToggleWordAppBarIconListener());
        initAddNewWordButton();
        showSuccessfulySavedMessage(findViewById(R.id.root));
        this.colors = new Colors(this);
        getLoaderManager().initLoader(LECTURE_LOADER, null, this);
        getLoaderManager().initLoader(WORDS_LOADER, null, this);


        findViewById(R.id.lectureName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WordListActivity.this, EditLectureActivity.class);
                i.putExtra(LectureListActivity.BOOK_LECTURES_URI_EXTRA, removeTwoLastSegment(bookLecturesWordsUri));
                i.putExtra(EditLectureActivity.BOOK_LECTURE_ID_URI_EXTRA, removeLastSegment(bookLecturesWordsUri));
                startActivity(i);
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case WORDS_LOADER:
                return new CursorLoader(this,
                        bookLecturesWordsUri,
                        Contract.getWordsColumns(),
                        Word.LECTURE_ID+"=?",
                        new String[]{getLectureIdAsString(bookLecturesWordsUri)},
                        Word.QUESTION);
            case LECTURE_LOADER :
            return new CursorLoader(this,
                    removeLastSegment(bookLecturesWordsUri),
                    null,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case WORDS_LOADER:
                if (data == null || data.getCount() == 0) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    ((WordListCursorAdapter) recyclerView.getAdapter()).swapCursor(data);
                }
                break;
            case LECTURE_LOADER:
                if(data != null && data.moveToNext()){
                    CollapsingToolbarLayout toolbar = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
                    toolbar.setTitleEnabled(true);
                    toolbar.setTitle(asString(data, "bookName"));
                    ((TextView)findViewById(R.id.lectureName))
                            .setText(asString(data, Lecture.NAME));
                }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((WordListCursorAdapter) recyclerView.getAdapter()).swapCursor(null);
    }

    private class WordListCursorAdapter extends RecyclerView.Adapter<WordViewHolder>  {

        private Cursor cursor;

         @Override
        public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_word_item, parent, false);
            return new WordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WordViewHolder holder, int position) {
            holder.bindItem(position, cursor);

        }
        @Override
        public int getItemCount() {
            return (cursor == null ? 0 : cursor.getCount());
        }

        public void swapCursor(Cursor newWordCursor) {
            if (cursor != null) {
                cursor.close();
            }
            cursor = newWordCursor;
            notifyDataSetChanged();
        }

    }

    private void verifyMultiselectorCallback(){
        if(multiSelectorCallback  == null)
            multiSelectorCallback = new ModalMultiSelectorCallback(this, multiSelector);

    }


    private class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private final static byte EDIT_ITEM = 1;
        private final static byte REMOVE_ITEM = 2;

        private RelativeLayout layout;
        private TextView question;
        private TextView answer;
        private CheckBox checkBox;
        private String id;
        private boolean activated;

        public WordViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            question = (TextView) itemView.findViewById(R.id.word_question);
            answer = (TextView) itemView.findViewById(R.id.word_answer);
            checkBox = (CheckBox) itemView.findViewById(R.id.wordCheckbox);
            layout = (RelativeLayout) itemView.findViewById(R.id.word_item);
        }


        @Override
        public void onClick(View view) {
            verifyMultiselectorCallback();
            boolean isChecked = checkBox.isChecked();
            if(isChecked){
                multiSelector.removeAt(id);
            }else{
                multiSelector.setSelected(id);
            }
            checkBox.setChecked(!isChecked);
            updateBackground(!isChecked);
            int selectedCount = multiSelector.getSelectedCount();
            if (actionMode == null && selectedCount > 0) {
                actionMode = startSupportActionMode(multiSelectorCallback);
            }else if(actionMode != null && selectedCount == 0){
                clearActionMode();
            }
            if(actionMode != null && selectedCount > 0){
                actionMode.setTitle("Selected: " + selectedCount);
            }

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem editMenu = menu.add(0,EDIT_ITEM,1,R.string.book_edit);
            MenuItem removeMenu = menu.add(0,REMOVE_ITEM,2,R.string.book_remove);
            removeMenu.setOnMenuItemClickListener(this);
            editMenu.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == EDIT_ITEM){
                Intent i = new Intent(WordListActivity.this, EditWordActivity.class);
                i.putExtra(BOOK_LECTURE_WORD_ID_EXTRA, withAppendedId(bookLecturesWordsUri, id));
                i.putExtra(BOOK_LECTURE_WORDS_URI_EXTRA, bookLecturesWordsUri);
                startActivity(i);
            }
            return false;
        }


        public void bindItem(int position, Cursor wordCursor) {

            if (wordCursor != null && wordCursor.moveToPosition(position)) {
                this.id = asString(wordCursor, Word._ID);
                question.setText( asString(wordCursor, Word.QUESTION) );
                answer.setText( asString(wordCursor, Word.ANSWER) );
                boolean isSelected = multiSelector.isSelected(id);
                this.activated = asBool(wordCursor, Word.ACTIVE);
                checkBox.setChecked(isSelected);
                updateBackground(isSelected);
            }
        }

        public void updateBackground(boolean isSelected){
            if(isSelected){
                layout.setBackgroundColor(colors.selected);
            }else if(this.activated){
                layout.setBackgroundColor(colors.activated);
            }else{
                layout.setBackgroundColor(colors.white);
            }
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle information) {
        information.putStringArrayList(SELECTION_POSITIONS, this.multiSelector.getAllIds());
    }

    @Override
    protected void onRestoreInstanceState(Bundle information) {
        ArrayList<String> selectedIds = information.getStringArrayList(SELECTION_POSITIONS);
        if(selectedIds != null){
            this.multiSelector.setIds(selectedIds);
        }
        super.onRestoreInstanceState(information);
    }

    private void initAddNewWordButton() {
        FloatingActionButton addLectureBtn = (FloatingActionButton) findViewById(R.id.addBtn);
        addLectureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WordListActivity.this, AddWordActivity.class);
                Intent intent = i.putExtra(BOOK_LECTURE_WORDS_URI_EXTRA, bookLecturesWordsUri);
                startActivity(i);
            }
        });
    }

    public void showLoader(){
        loader.setVisibility(View.VISIBLE);
    }

    public void hideLoader(){
        loader.setVisibility(View.GONE);
    }

    public Uri getUri(){
        return bookLecturesWordsUri;
    }


    public void reloadView(){
        clearActionMode();
        getLoaderManager().restartLoader(WORDS_LOADER, null, this);
        hideLoader();

    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    private void clearActionMode(){
        if(actionMode != null){
            actionMode.finish();
            actionMode = null;
            multiSelector.clear();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.removeAll:
                Log.i(TAG, "Removing all cards..");
                new WordListAsyncQueryHelper(this).removeAllInLecture();
                return true;
        }
        return false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.word_list_menu, menu);
        return true;
    }


    static class Colors{

        int white;
        int selected;
        int activated;

        Colors(Context context){
            this.selected = ContextCompat.getColor(context, R.color.selected);
            this.activated = ContextCompat.getColor(context, R.color.activated);
            this.white = ContextCompat.getColor(context, R.color.md_white_1000);
        }
    }
}


