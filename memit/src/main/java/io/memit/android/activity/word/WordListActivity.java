package io.memit.android.activity.word;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.memit.android.R;
import io.memit.android.activity.AbstractActivity;
import io.memit.android.activity.lecture.LectureListActivity;
import io.memit.android.activity.lecture.ToggleAppBarIconListener;
import io.memit.android.provider.Contract;
import io.memit.android.provider.Contract.Word;

import static android.content.ContentUris.withAppendedId;
import static io.memit.android.tools.CursorUtils.asInt;
import static io.memit.android.tools.CursorUtils.asString;
import static io.memit.android.tools.UriUtils.removeLastSegment;

/**
 * Created by peter on 2/23/17.
 */

public class WordListActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG =  WordListActivity.class.getSimpleName();
    private static final byte WORDS_LOADER = 1;

    public final static String BOOK_LECTURES_URI_EXTRA = "booklecturesWordsUri";

    private RecyclerView recyclerView;
    private TextView empty;
    private Uri bookLecturesWordsUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.lecture_toolbar);
        bookLecturesWordsUri = getIntent().getExtras().getParcelable(BOOK_LECTURES_URI_EXTRA);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        useBackButtonIn(toolbar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WordListActivity.this, LectureListActivity.class);
                i.putExtra(LectureListActivity.BOOK_LECTURES_URI_EXTRA, removeLastSegment(bookLecturesWordsUri));
                startActivity(i);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new WordListActivity.WordListCursorAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        empty = (TextView) findViewById(R.id.empty);

        //getLoaderManager().initLoader(BOOK_LOADER, null, this);
        //getLoaderManager().initLoader(LECTURE_LOADER, null, this);
        appBarLayout.addOnOffsetChangedListener(new ToggleAppBarIconListener());
        //initAddNewLectureButton();
        showSuccessfulySavedMessage(findViewById(R.id.root));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case WORDS_LOADER:
                return new CursorLoader(this,
                        bookLecturesWordsUri,
                        Contract.getWordsColumns(),
                         Word.LECTURE_ID+"=?",
                        new String[]{bookLecturesWordsUri.getLastPathSegment()},
                        Word.QUESTION);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case WORDS_LOADER:

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
            if (cursor != null && cursor.moveToPosition(position)) {
                int id = asInt(cursor, Word._ID);
                holder.question.setText( asString(cursor, Word.QUESTION) );
                holder.answer.setText( asString(cursor, Word.QUESTION) );
                holder.uri = withAppendedId(bookLecturesWordsUri, id);
            }
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



    private class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private final static byte EDIT_ITEM = 1;
        private final static byte REMOVE_ITEM = 2;

        private TextView question;
        private TextView answer;
        private Uri uri;

        public WordViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            question = (TextView) itemView.findViewById(R.id.word_question);
            answer = (TextView) itemView.findViewById(R.id.word_answer);
        }

        @Override
        public void onClick(View view) {

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

            return false;
        }


    }
}


