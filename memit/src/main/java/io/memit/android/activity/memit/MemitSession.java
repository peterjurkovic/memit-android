package io.memit.android.activity.memit;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.memit.android.BuildConfig;
import io.memit.android.provider.Contract;

import static io.memit.android.provider.Contract.SyncColumns.ACTIVE_WORD_COUNT;
import static io.memit.android.tools.CursorUtils.asShort;
import static io.memit.android.tools.CursorUtils.asString;

/**
 * Created by peter on 5/1/17.
 */

public class MemitSession {

    private final static String TAG = MemitSession.class.getSimpleName();

    public final static byte QUEUE_SIZE_LIMIT = 5;

    final String id;
    private final Deque<Memo> queue = new ArrayDeque<Memo>(QUEUE_SIZE_LIMIT + 1);
    final AtomicInteger activated;
    final AtomicBoolean persited;

    private MemitSession(SessionBuilder builder){
        this.persited = new AtomicBoolean(builder.sessionPersited);
        this.id = builder.sessionId;
        this.activated  = new AtomicInteger(builder.activated);
        queue.addAll(builder.list);
    }

    public synchronized Memo poll() {
        return queue.pollFirst();
    }


    public void add(Cursor cursor) {
        if(cursor.moveToNext()){
            add( Memo.map(cursor) );
        }
    }

    public synchronized void add(Memo memo) {
        if(BuildConfig.DEBUG){
            Log.d(TAG, "Adding new memo to the queue: " + memo);
        }
        if(memo != null && ! queue.contains(memo) ){
            queue.add(memo);
        }
    }

    public boolean hasEnded(){
        return hasNoWordActivated();
    }

    public List<String> getIdInQueue(){
        List<String> ids = new ArrayList<>(queue.size());
        for(Memo memo: queue){
            ids.add(memo.id);
        }
        return ids;
    }

    public boolean hasNoWordActivated(){
        return activated.get() == 0;
    }

    public int getActivatedCount(){
        return activated.get();
    }

    public int queueSize(){
        return queue.size();
    }


    public boolean isLoadableFromDatabase(){
        return getActivatedCount() >= QUEUE_SIZE_LIMIT;
    }


    public int decrementAndGet(){
        return this.activated.decrementAndGet();
    }

    @Override
    public String toString() {
        return "MemitSession{" +
                ", id='" + id + '\'' +
                ", activatedCount=" + activated +
                ", QUEUE_SIZE_LIMIT=" + queueSize() +
                '}';
    }

    static class SessionBuilder{
        String sessionId;
        int activated  = -1;
        List<Memo> list;
        boolean sessionPersited;

        public static SessionBuilder create(){
            return new SessionBuilder();
        }

        public synchronized void setSession(Cursor cursor){
            this.sessionPersited = cursor.moveToNext();
            if( sessionPersited ){
                this.sessionId = asString(cursor, Contract.Session._ID );
            }else{
                this.sessionId = UUID.randomUUID().toString();
            }
        }

        public synchronized void setActivated(Cursor cursor){
            if(cursor.moveToNext()){
                this.activated = asShort(cursor, ACTIVE_WORD_COUNT);
            }else{
                this.activated = 0;
            }
        }

        public synchronized void setMemos(Cursor cursor){
            list = new ArrayList<>(8);
            while (cursor.moveToNext()){
                list.add( Memo.map(cursor ));
            }
        }

        public MemitSession build(){
            return new MemitSession(this);
        }

        public synchronized boolean isFullyInicialized(){
            return sessionId != null &&
                    activated > -1 &&
                    list != null;
        }

    }
}
