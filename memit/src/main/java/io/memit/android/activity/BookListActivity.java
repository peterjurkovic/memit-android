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

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import io.memit.android.BuildConfig;
import io.memit.android.R;
import io.memit.android.provider.BookContract;

/**
 * Created by peter on 1/31/17.
 */

public class BookListActivity extends AbstractActivity implements  LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG =  BookListActivity.class.getSimpleName();

    private static final int LOADER_ID_BOOK = 1;
    private RecyclerView recyclerView;
    private TextView empty;
    private CoordinatorLayout root;
    private RemoveBookHandler removeBookHandler;
    private BookListActivity context = this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        root = (CoordinatorLayout) findViewById(R.id.root);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        empty = (TextView) findViewById(R.id.empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BookListCursorAdapter());
        removeBookHandler = new RemoveBookHandler();
        getLoaderManager().initLoader(LOADER_ID_BOOK, null, this);

        // Account account = new Account("SyncAccount", "stubAuthenticator");
        // AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);

        FloatingActionButton addBookBtn = (FloatingActionButton) findViewById(R.id.addBookBtn);
        addBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddBookActivity.class);
                startActivity(i);
            }
        });
        new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_book)
                .sizeDp(24);

        initDrawer(toolbar,savedInstanceState);
        if(BuildConfig.DEBUG) Log.d(TAG, "created");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader = null;
        String[] projection = {
                BookContract.Book._ID,
                BookContract.Book.NAME
        };
        Log.d(TAG, "onCreateLoader id:" + id + " budle:" + args);
        switch (id) {
            case LOADER_ID_BOOK:
                loader = new CursorLoader(this,
                        BookContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        BookContract.Book.NAME);
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
                        .getString(bookCursor.getColumnIndexOrThrow(BookContract.Book.NAME));

                int id = bookCursor
                        .getInt(bookCursor.getColumnIndexOrThrow(BookContract.Book._ID));

                holder.name.setText(name);
                holder.id = id;
                Log.d(TAG, holder.toString());
                holder.uri = ContentUris.withAppendedId(BookContract.CONTENT_URI,  id);
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

        public TextView name;
        public Uri uri;
        private int id;

        public BookViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            name = (TextView) itemView.findViewById(R.id.book_title);
        }

        @Override
        public void onClick(View view) {

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Log.d(TAG, "onCreateContextMenu");

            MenuItem editMenu = menu.add(0,EDIT_ITEM,1,R.string.book_edit);
            MenuItem removeMenu = menu.add(0,REMOVE_ITEM,2,R.string.book_remove);
            removeMenu.setOnMenuItemClickListener(this);
            editMenu.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Log.d(TAG, "onMenuItemClick " + item);
            switch (item.getItemId()){
                case EDIT_ITEM:
                    break;
                case REMOVE_ITEM:
                    remove();
                    break;
            }
            /*
            Intent detailIntent = new Intent(  BookListActivity.class);
            detailIntent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_URI, uri);
            startActivity(detailIntent);
            */
            return false;
        }


        private void remove(){
            Log.i(TAG, "Removing: " + this + " as post: " + getAdapterPosition());
            // recyclerView.getAdapter().notifyItemRemoved(getAdapterPosition());
            // recyclerView.getAdapter().notifyDataSetChanged();
            removeBookHandler.startDelete(-1, name, uri,null,null);
        }

    }


     private class RemoveBookHandler extends AsyncQueryHandler{

         public RemoveBookHandler() {
             super(getContentResolver());
         }

         @Override
         public void startQuery(int token, Object cookie, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
             super.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
         }

         @Override
         protected void onDeleteComplete(int position, Object cookie, int result) {
             Log.d(TAG, "onDeleteComplete " + cookie);
             getLoaderManager().restartLoader(LOADER_ID_BOOK, null, context);
             Snackbar.make(root, "Book has been removed", Snackbar.LENGTH_SHORT).show();
         }
     }

}
