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
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

import io.memit.android.R;
import io.memit.android.activity.AbstractActivity;
import io.memit.android.provider.Contract.Lecture;

/**
 * Created by peter on 2/16/17.
 */

public class LectureListActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG =  LectureListActivity.class.getSimpleName();
    private static final byte LECTURE_LOADER = 1;

    public final static String BOOK_ID_EXTRA = "bookIdExtra";
    public final static String BOOK_NAME_EXTRA = "bookNameExtra";

    private RecyclerView recyclerView;
    private TextView empty;
    private long bookdId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lecture_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.lecture_toolbar);
        bookdId = getIntent().getExtras().getLong(BOOK_ID_EXTRA);
        String bookName = getIntent().getExtras().getString(BOOK_NAME_EXTRA);
        toolbar.setTitle(bookName);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        empty = (TextView) findViewById(R.id.empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LectureListCursorAdapter());

        getLoaderManager().initLoader(LECTURE_LOADER, null, this);

        appBarLayout.addOnOffsetChangedListener(new ToggleAppBarIconListener());

        FloatingActionButton addBookBtn = (FloatingActionButton) findViewById(R.id.addBookBtn);
        addBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LectureListActivity.this, AddLectureActivity.class);
                i.putExtra(BOOK_ID_EXTRA, bookdId);
                startActivity(i);
            }
        });

        initDrawer(toolbar,savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LECTURE_LOADER:
                return new CursorLoader(this,
                        ContentUris.withAppendedId(Lecture.CONTENT_URI, bookdId),
                        null,
                        null,
                        null,
                        Lecture.NAME);
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

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((LectureListCursorAdapter) recyclerView.getAdapter()).swapCursor(null);
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
                String name = cursor
                        .getString(cursor.getColumnIndexOrThrow(Lecture.NAME));
                int id = cursor.
                        getInt(cursor.getColumnIndexOrThrow(Lecture._ID));
                int wordCount = cursor
                        .getInt(cursor.getColumnIndexOrThrow(Lecture.WORD_COUNT));
                int activeWordCount = cursor
                        .getInt(cursor.getColumnIndexOrThrow(Lecture.ACTIVE_WORD_COUNT));
                holder.name.setText(name);
                holder.info.setText(getString(R.string.lecutre_item_info, wordCount, activeWordCount));

                holder.id = id;
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
        private long id;

        public LectureViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            name = (TextView) itemView.findViewById(R.id.lecture_title);
            info = (TextView) itemView.findViewById(R.id.lecture_info);
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
            Log.d(TAG, item.getItemId() + "");
            return false;
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.lectureBookEdit :

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
