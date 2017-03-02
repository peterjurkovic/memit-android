package io.memit.android.activity.lecture;

import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.memit.android.R;
import io.memit.android.activity.AbstractActivity;
import io.memit.android.activity.BookListActivity;
import io.memit.android.activity.EditBookActivity;
import io.memit.android.activity.word.WordListActivity;
import io.memit.android.provider.Contract.Book;
import io.memit.android.provider.Contract.Lecture;

import static io.memit.android.activity.lecture.EditLectureActivity.BOOK_LECTURE_ID_URI_EXTRA;
import static io.memit.android.tools.CursorUtils.asInt;
import static io.memit.android.tools.CursorUtils.asString;
import static io.memit.android.tools.UriUtils.removeLastSegment;

/**
 * Created by peter on 2/16/17.
 */

public class LectureListActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG =  LectureListActivity.class.getSimpleName();
    private static final byte LECTURE_LOADER = 1;
    private static final byte BOOK_LOADER = 2;

    public final static String BOOK_LECTURES_URI_EXTRA = "booklecturesUri";

    private RecyclerView recyclerView;
    private TextView empty;
    // books/{id}/lectures
    private Uri bookLecturesUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lecture_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.lecture_toolbar);
        bookLecturesUri = getIntent().getExtras().getParcelable(BOOK_LECTURES_URI_EXTRA);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        useBackButtonIn(toolbar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LectureListActivity.this, BookListActivity.class));
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new LectureListCursorAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        empty = (TextView) findViewById(R.id.empty);

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
        getLoaderManager().initLoader(LECTURE_LOADER, null, this);
        appBarLayout.addOnOffsetChangedListener(new ToggleAppBarIconListener());
        initAddNewLectureButton();
        showSuccessfulySavedMessage(findViewById(R.id.root));
    }

    private void initAddNewLectureButton() {
        FloatingActionButton addLectureBtn = (FloatingActionButton) findViewById(R.id.addBookBtn);
        addLectureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LectureListActivity.this, AddLectureActivity.class);
                i.putExtra(BOOK_LECTURES_URI_EXTRA, bookLecturesUri);
                startActivity(i);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LECTURE_LOADER:
                return new CursorLoader(this,
                        bookLecturesUri,
                        null,
                        null,
                        null,
                        Lecture.NAME);
            case BOOK_LOADER:
                return new CursorLoader(this,
                        removeLastSegment(bookLecturesUri),
                        new String[]{Book.NAME},
                        null,
                        null,
                        null
                        );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LECTURE_LOADER){

            if (data == null || data.getCount() == 0) {
                empty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                empty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                ((LectureListCursorAdapter) recyclerView.getAdapter()).swapCursor(data);
            }

        }else if (loader.getId() == BOOK_LOADER){

            if(data != null && data.moveToNext()){
                CollapsingToolbarLayout toolbar = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
                toolbar.setTitleEnabled(true);
                toolbar.setTitle(asString(data, Book.NAME));
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((LectureListCursorAdapter) recyclerView.getAdapter()).swapCursor(null);
    }

    public void refresList(){
        getLoaderManager().restartLoader(LECTURE_LOADER, null, this);
    }




    private class LectureListCursorAdapter extends RecyclerView.Adapter<LectureListActivity.LectureViewHolder>  {

        private Cursor cursor;

        @Override
        public LectureListActivity.LectureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_lecture_item, parent, false);
            return new LectureListActivity.LectureViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LectureListActivity.LectureViewHolder holder, int position) {
            if (cursor != null && cursor.moveToPosition(position)) {
                String name =           asString(cursor, Lecture.NAME);
                int id =                asInt(cursor, Lecture._ID);
                int wordCount =         asInt(cursor, Lecture.WORD_COUNT);
                int activeWordCount =   asInt(cursor, Lecture.ACTIVE_WORD_COUNT);
                holder.name.setText(name);
                holder.info.setText(getString(R.string.lecutre_item_info, wordCount, activeWordCount));
                holder.uri = ContentUris.withAppendedId(bookLecturesUri, id);
            }
        }

        @Override
        public int getItemCount() {
            return (cursor == null ? 0 : cursor.getCount());
        }

        public void swapCursor(Cursor newBookListCursor) {
            if (cursor != null) {
                cursor.close();
            }
            cursor = newBookListCursor;
            notifyDataSetChanged();
        }
    }



    private class LectureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private final static byte EDIT_ITEM = 1;
        private final static byte REMOVE_ITEM = 2;

        private TextView name;
        private TextView info;
        private Uri uri;

        public LectureViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            name = (TextView) itemView.findViewById(R.id.lecture_title);
            info = (TextView) itemView.findViewById(R.id.lecture_info);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(LectureListActivity.this, WordListActivity.class);
            i.putExtra(WordListActivity.BOOK_LECTURES_URI_EXTRA, uri.withAppendedPath(uri, "words"));
            startActivity(i);
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
                Intent i = new Intent(LectureListActivity.this, EditLectureActivity.class);
                i.putExtra(BOOK_LECTURES_URI_EXTRA, bookLecturesUri);
                i.putExtra(BOOK_LECTURE_ID_URI_EXTRA, uri);
                startActivity(i);
            }

            if(item.getItemId() == REMOVE_ITEM){
               new LectureAsyncQueryHandler(LectureListActivity.this).removeLecture(uri);
            }
            return false;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.lectureBookEdit :
                Intent intent = new Intent(this, EditBookActivity.class);
                intent.putExtra(EditBookActivity.BOOK_EXTRA_URI, removeLastSegment(bookLecturesUri));
                startActivity(intent);
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.lecture_list_menu, menu);
        return true;
    }
}
