package io.memit.android.activity.memit;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

import io.memit.android.activity.memit.MemitSession.SessionBuilder;
import io.memit.android.provider.Contract.Session;
import io.memit.android.provider.Contract.SessionActivity;
import io.memit.android.provider.Contract.Word;
import io.memit.android.tools.StringsUtils;

import static io.memit.android.provider.Contract.SyncColumns.ACTIVE_WORD_COUNT;
import static io.memit.android.tools.StringsUtils.inClause;

/**
 * Created by peter on 5/1/17.
 */

public class MemitSessionManager extends AsyncQueryHandler {

    private final byte INIT_SESSION_ACTION = 1;
    private final byte LOAD_ACTIVATED_COUNT = 2;
    private final byte LOAD_MEMOS_ACTION= 3;
    private final byte LOAD_NEXT= 4;

    private MemitSession session;
    private final Context context;


    public MemitSessionManager(MemitActivity context){
        super(context.getContentResolver());
        this.context = context;
    }

    public void sessionStart(){
        if( session == null ){
            final SessionBuilder builder = SessionBuilder.create();

            // loads number of activated cards
            startQuery(LOAD_ACTIVATED_COUNT,
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
                    SessionActivity.CONTENT_URI, null,
                    null, null, null);

        }
    }

    public Memo getNext(){
        Memo memo = session.poll();
        if(memo == null){
            if(context instanceof  OnSessionEndedListener){
                ((OnSessionEndedListener)context).onSessionEnded(session);
                return null;
            }
        }
        return memo;
    }

    private void loadNext(){
        if(  session.hasNext() ){

            // public void startQuery(int token, Object cookie, Uri uri,
            //        String[] projection, String selection, String[] selectionArgs,
            //        String orderBy)
            String where = null;
            List<String> ids = session.getIdInQueue();
            if( ! ids.isEmpty()){
                where = " w.id NOT " + inClause(session.getIdInQueue());
            }

            startQuery(LOAD_NEXT,
                    null,
                    SessionActivity.CONTENT_URI, null,
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

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token){
            case INIT_SESSION_ACTION:
                ((SessionBuilder)cookie).setSession(cursor);
                break;
            case LOAD_MEMOS_ACTION:
                ((SessionBuilder)cookie).setMemos(cursor);
                break;
            case LOAD_ACTIVATED_COUNT:
                ((SessionBuilder)cookie).setActivated(cursor);
                break;
            case LOAD_NEXT:
                session.add(cursor);
                break;
        }

        if(cookie instanceof  SessionBuilder &&
                ((SessionBuilder)cookie).isFullyInicialized()){
            this.session = ((SessionBuilder)cookie).build();
            Log.i("session", session.toString());
            if(this.session.hasNoWordActivated()){
                ((OnSessionNotStartedListener) context).onSessionNotStarted();
            }else if(context instanceof OnSessionCreatedListener){
                ((OnSessionCreatedListener) context).onSessionCreated(session);
            }
        }
    }


}
