package io.memit.android.activity;

import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import io.memit.android.R;
import io.memit.android.activity.lecture.LectureListActivity;
import io.memit.android.provider.Contract.Book;

/**
 * Created by peter on 1/31/17.
 */

public class BookListActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG =  BookListActivity.class.getSimpleName();
    private static final byte LOADER_ID_BOOK = 1;
    public final static String LECTURE_EXTRA_URI = "lectureUri";

    private RecyclerView recyclerView;
    private TextView empty;
    private CoordinatorLayout root;
    private RemoveBookHandler removeBookHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_dynamic);
        toolbar.setTitle(R.string.book_list);
        setSupportActionBar(toolbar);
        root = (CoordinatorLayout) findViewById(R.id.root);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        empty = (TextView) findViewById(R.id.empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BookListCursorAdapter());
        removeBookHandler = new RemoveBookHandler(this);
        getLoaderManager().initLoader(LOADER_ID_BOOK, null, this);


        FloatingActionButton addBookBtn = (FloatingActionButton) findViewById(R.id.addBookBtn);
        addBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddBookActivity.class);
                startActivity(i);
            }
        });

        initDrawer(toolbar,savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader = null;
        String[] projection = {
                Book._ID,
                Book.NAME
        };
        Log.d(TAG, "onCreateLoader id:" + id + " budle:" + args);
        switch (id) {
            case LOADER_ID_BOOK:
                loader = new CursorLoader(this,
                        Book.CONTENT_URI,
                        projection,
                        null,
                        null,
                        Book.NAME);
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished, count: " + data.getCount());
        if (data == null || data.getCount() == 0) {
            empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            ((BookListCursorAdapter) recyclerView.getAdapter()).swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((BookListCursorAdapter) recyclerView.getAdapter()).swapCursor(null);
    }



    private class BookListCursorAdapter extends RecyclerView.Adapter<BookViewHolder>  {

        private Cursor bookCursor;

        @Override
        public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "[onCreateViewHolder] viewType" + viewType);
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_book_item, parent, false);

            return new BookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookViewHolder holder, int position) {
            if (bookCursor != null && bookCursor.moveToPosition(position)) {
                String name = bookCursor
                        .getString(bookCursor.getColumnIndexOrThrow(Book.NAME));

                int id = bookCursor
                        .getInt(bookCursor.getColumnIndexOrThrow(Book._ID));

                int lectureCount = bookCursor
                        .getInt(bookCursor.getColumnIndexOrThrow(Book.LECTURE_COUNT));

                int wordCount = bookCursor
                        .getInt(bookCursor.getColumnIndexOrThrow(Book.WORD_COUNT));

                int activeWordCount = bookCursor
                        .getInt(bookCursor.getColumnIndexOrThrow(Book.ACTIVE_WORD_COUNT));

                holder.name.setText(name);
                holder.info.setText(getString(
                        R.string.book_item_info,lectureCount, wordCount, activeWordCount));
                holder.id = id;
                holder.title = name;
                Log.d(TAG, holder.toString());
                holder.uri = ContentUris.withAppendedId(Book.CONTENT_URI,  id);
            }
        }



        @Override
        public int getItemCount() {
            return (bookCursor == null ? 0 : bookCursor.getCount());
        }

        public void swapCursor(Cursor newBookListCursor) {
            if (bookCursor != null) {
                bookCursor.close();
            }

            bookCursor = newBookListCursor;
            notifyDataSetChanged();
        }
    }

    private class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private final static byte EDIT_ITEM = 1;
        private final static byte REMOVE_ITEM = 2;

        private TextView name;
        private TextView info;
        private Uri uri;
        private int id;
        private String title;

        public BookViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            name = (TextView) itemView.findViewById(R.id.book_title);
            info = (TextView) itemView.findViewById(R.id.book_info);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(BookListActivity.this, LectureListActivity.class);
            intent.putExtra(LectureListActivity.BOOK_ID_EXTRA, id);
            startActivity(intent);
            // overridePendingTransitionEnter();
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
            switch (item.getItemId()){
                case EDIT_ITEM:
                    editBook();
                    break;
                case REMOVE_ITEM:
                    removeBook();
                    break;
            }
            return false;
        }

        private void editBook(){
            Intent intent = new Intent(BookListActivity.this, EditBookActivity.class);
            intent.putExtra(EditBookActivity.BOOK_EXTRA_URI, uri);
            startActivity(intent);
        }


        private void removeBook(){
            Log.i(TAG, "Removing: " + this + " as post: " + getAdapterPosition());
            removeBookHandler.startDelete(-1, name, uri, null, null);
        }

    }


     private static class RemoveBookHandler extends AsyncQueryHandler{

         private final WeakReference<BookListActivity> weekActivity;

         public RemoveBookHandler(BookListActivity activity) {
             super(activity.getContentResolver());
             this.weekActivity = new WeakReference<>(activity);
         }

         @Override
         public void startQuery(int token, Object cookie, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
             super.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
         }

         @Override
         protected void onDeleteComplete(int position, Object cookie, int result) {
             BookListActivity activity = weekActivity.get();
             if(activity == null || activity.isFinishing()){
                 return;
             }
             activity.getLoaderManager().restartLoader(LOADER_ID_BOOK, null, activity);
             Snackbar.make(activity.root, R.string.book_removed, Snackbar.LENGTH_SHORT).show();
         }
     }

}
