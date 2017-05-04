package io.memit.android.activity.memit;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;

import io.memit.android.provider.Contract;
import io.memit.android.provider.Contract.Session;

import static io.memit.android.tools.CursorUtils.asString;

/**
 * Created by peter on 5/1/17.
 */

public class MemitSessionManager extends AsyncQueryHandler {

    private final byte INIT_SESSION_ACTION = 1;
    private final byte LOAD_MEMOS_ACTION= 2;

    private MemitSession session;
    private final Context context;


    public MemitSessionManager(MemitActivity context){
        super(context.getContentResolver());
        this.context = context;
    }

    public void sessionStart(){
        if( session == null )
        startQuery(INIT_SESSION_ACTION, null, Session.CONTENT_URI, new String[]{Session._ID},
                "date(created)=date('now')", null, null);
    }

    public Memo getNext(){
        Memo memo = session.take();
        if(memo == null){
            if(context instanceof  OnSessionEndedListener){
                ((OnSessionEndedListener)context).onSessionEnded(session);
                return null;
            }
        }
        return memo;
    }

    public MemitSession getSession(){
        return session;
    }


    public interface OnSessionEndedListener{

        void onSessionEnded(MemitSession session);
    }


    public interface OnSessionStartedListener{

        void onSessionStarted();

    }

    public interface OnNoMemoActivatedListener {

        void onNoMemoActivated();

    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if(INIT_SESSION_ACTION == token){
            onSessionCreated(cursor);
        }else if(LOAD_MEMOS_ACTION == token){
            onSessonMemosLoaded(cursor);
        }
    }


    private void loadInitSessionMemos(){
        startQuery(LOAD_MEMOS_ACTION, null, Contract.SessionActivity.CONTENT_URI, null,
                null, null, null);
    }




    private void onSessionCreated(Cursor cursor){
        if(cursor.moveToNext()){
            session = MemitSession.create(asString(cursor,Session._ID ));
        }else{
            session = MemitSession.create();
        }
        loadInitSessionMemos();

    }

    private void onSessonMemosLoaded(Cursor cursor){
        if(cursor.moveToNext()){
            session.add( Memo.map(cursor ));
            while (cursor.moveToNext()){
                session.add( Memo.map(cursor ));
            }
            if(context instanceof OnSessionStartedListener ){
                ((OnSessionStartedListener) context)
                        .onSessionStarted();
            }
        }else{
            if(context instanceof OnNoMemoActivatedListener){
                ((OnNoMemoActivatedListener) context)
                        .onNoMemoActivated();
            }
        }
    }
}
