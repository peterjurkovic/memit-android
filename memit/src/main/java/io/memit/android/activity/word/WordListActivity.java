package io.memit.android.activity.word;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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
import io.memit.android.provider.Contract;
import io.memit.android.provider.Contract.Lecture;
import io.memit.android.provider.Contract.Word;

import static android.content.ContentUris.withAppendedId;
import static io.memit.android.activity.word.BaseWordActivity.BOOK_LECTURE_WORDS_URI_EXTRA;
import static io.memit.android.tools.CursorUtils.asInt;
import static io.memit.android.tools.CursorUtils.asString;
import static io.memit.android.tools.UriUtils.getLectureIdAsString;
import static io.memit.android.tools.UriUtils.removeLastSegment;
import static io.memit.android.tools.UriUtils.removeTwoLastSegment;

/**
 * Created by peter on 2/23/17.
 */

public class WordListActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG =  WordListActivity.class.getSimpleName();
    private static final byte WORDS_LOADER = 1;
    private static final byte LECTURE_LOADER = 2;

    public final static String BOOK_LECTURES_URI_EXTRA = "booklecturesWordsUri";

    private RecyclerView recyclerView;
    private TextView empty;
    // books/{id}/lectures/{id}/words
    private Uri bookLecturesWordsUri;

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

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new WordListActivity.WordListCursorAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        empty = (TextView) findViewById(R.id.empty);


        appBarLayout.addOnOffsetChangedListener(new ToggleWordAppBarIconListener());
        initAddNewWordButton();
        showSuccessfulySavedMessage(findViewById(R.id.root));
        getLoaderManager().initLoader(LECTURE_LOADER, null, this);
        getLoaderManager().initLoader(WORDS_LOADER, null, this);
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
}


