package io.memit.android.activity.memit;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

import io.memit.android.activity.memit.MemitSession.SessionBuilder;
import io.memit.android.provider.Contract.MemiStrategy;
import io.memit.android.provider.Contract.Session;
import io.memit.android.provider.Contract.SessionWord;
import io.memit.android.provider.Contract.Word;

import static io.memit.android.provider.Contract.SyncColumns.ACTIVE_WORD_COUNT;
import static io.memit.android.tools.StringsUtils.inClause;

/**
 * Created by peter on 5/1/17.
 */

public class MemitSessionManager extends AsyncQueryHandler {

    private final static String TAG = MemitSessionManager.class.getSimpleName();

    private final byte INIT_SESSION_ACTION = 1;
    private final byte PERSISTS_SESSION_ACTION = 5;
    private final byte LOAD_ACTIVATED_COUNT_ACTION = 2;
    private final byte LOAD_MEMOS_ACTION= 3;
    private final byte LOAD_NEXT_ACTION = 4;
    private final byte DEACTIVATE_ACTION = 6;
    private final byte UPDATE_RATING_ACTION = 7;

    private MemitSession session;
    private final MemitActivity context;


    public MemitSessionManager(MemitActivity context){
        super(context.getContentResolver());
        this.context = context;
    }

    public void sessionStart(){
        if( session == null ){
            final SessionBuilder builder = SessionBuilder.create();

            startQuery(LOAD_ACTIVATED_COUNT_ACTION,
                    builder,
                    Word.CONTENT_URI,
                    new String[]{"count(*) as " + ACTIVE_WORD_COUNT},
                    "active = 1", null, null);

            // loads today's session ID
            startQuery(INIT_SESSION_ACTION,
                    builder,
                    Session.CONTENT_URI,
                    new String[]{Session._ID},
                    "date(created)=date('now')", null, null);

            // loads first X-memos, where X is a limit
            startQuery(LOAD_MEMOS_ACTION,
                    builder,
                    Session.INIT_URI, null,
                    null, null, null);

        }
    }


    public Memo rateAndGetNext(Memo memo, Rating rating){
        ensurePersistedSession();
        switch (rating){
            case KNOW:
                context.onNumberOfActiveCardsChanged(session.decrementAndGet());
            case NEUTRAL:
            case DONT_KNOW:
                updateRating(memo, rating);
        }
        return getNext();
    }

    private void addToQueue(Memo memo){

    }



    public Memo getNext(){
        Memo memo = session.poll();
        Log.i("SessionManager", "Next: "+memo + " left:" + session.queueSize());
        if(memo == null){
            context.onSessionEnded(session);
            return null;
        }else{
            loadNextAsync();
        }
        return memo;
    }

    private void loadNextAsync(){
        if(  session.isLoadableFromDatabase() ){
            Log.i(TAG, "Loading next memo");

            String where = null;
            List<String> ids = session.getIdInQueue();
            if( ! ids.isEmpty()){
                where = " AND w._id NOT " + inClause(session.getIdInQueue());
            }

            startQuery(LOAD_NEXT_ACTION,
                    null,
                    MemiStrategy.SEQUENCE_STRATEGY_URI,
                    null,
                    where, null, null);
        }
    }

    public MemitSession getSession(){
        return session;
    }


    public interface OnSessionEndedListener{
        void onSessionEnded(MemitSession session);
    }


    public interface OnSessionCreatedListener {
        void onSessionCreated(MemitSession session);
    }

    public interface OnNoMemoActivatedListener {
        void onNoMemoActivated();
    }

    public interface  OnSessionNotStartedListener{
        void onSessionNotStarted();
    }

    public interface OnNumberOfActiveCardsChangedListener{
        void onNumberOfActiveCardsChanged(int currentNumber);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token){
            case INIT_SESSION_ACTION:
                ((SessionBuilder)cookie).setSession(cursor);
                break;
            case LOAD_MEMOS_ACTION:
                ((SessionBuilder)cookie).setMemos(cursor);
                break;
            case LOAD_ACTIVATED_COUNT_ACTION:
                ((SessionBuilder)cookie).setActivated(cursor);
                break;
            case LOAD_NEXT_ACTION:
                session.add(cursor);
                break;

        }

        if(cookie instanceof  SessionBuilder &&
                ((SessionBuilder)cookie).isFullyInicialized()){
            this.session = ((SessionBuilder)cookie).build();
            Log.i(TAG,session.toString());
            if(this.session.hasNoWordActivated()){
                context.onSessionNotStarted();
            }else{
                context.onSessionCreated(session);
                context.onNumberOfActiveCardsChanged(session.getActivatedCount());
            }
        }
    }

    private void ensurePersistedSession(){
        if( session.persited.compareAndSet(false, true) ){
            ContentValues cv = new ContentValues();
            cv.put(Session._ID, session.id);
            startInsert(INIT_SESSION_ACTION, null, Session.CONTENT_URI, cv);
            Log.i(TAG, "Session has been persisted");
        }else {
            Log.d(TAG, "Session already persisted");
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        switch (token){
            case UPDATE_RATING_ACTION:
                Log.i(TAG, "Memo updated: " + cookie);
                break;
        }
    }

    protected void updateRating(Memo memo, Rating rating){
        ContentValues cv = new ContentValues();
        cv.put(SessionWord.LAST_RATING, rating.value());
        cv.put(SessionWord.SESSION_ID, session.id);
        cv.put(SessionWord.WORD_ID, memo.id);
        startUpdate(UPDATE_RATING_ACTION,
                memo,
                SessionWord.RATE_URI,
                cv,
                null,
                null);
    }
}
