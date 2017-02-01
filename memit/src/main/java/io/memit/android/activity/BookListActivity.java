package io.memit.android.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.memit.android.BaseActivity;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        empty = (TextView) findViewById(R.id.empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BookListCursorAdapter());

        getLoaderManager().initLoader(LOADER_ID_BOOK, null, this);

        Account account = new Account("SyncAccount", "stubAuthenticator");

        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);

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
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_book_item, parent, false);

            return new BookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookViewHolder holder, int position) {
            if (bookCursor != null
                    && bookCursor.moveToPosition(position)) {
                String name = bookCursor
                        .getString(bookCursor.getColumnIndexOrThrow(BookContract.Book.NAME));

                int id = bookCursor
                        .getInt(bookCursor.getColumnIndexOrThrow(BookContract.Book._ID));

                holder.name.setText(name);
                holder.uri = ContentUris.withAppendedId(BookContract.CONTENT_URI,  id);
            }
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public void swapCursor(Cursor newDeviceCursor) {
            if (bookCursor != null) {
                bookCursor.close();
            }

            bookCursor = newDeviceCursor;

            notifyDataSetChanged();
        }
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public Uri uri;

        public BookViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View view) {
            Intent detailIntent = new Intent(view.getContext(),  BaseActivity.class);

            //detailIntent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_URI, uri);
            // startActivity(detailIntent);
        }
    }

}
