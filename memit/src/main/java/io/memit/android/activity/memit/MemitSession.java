package io.memit.android.activity.memit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.synchronizedList;

/**
 * Created by peter on 5/1/17.
 */

public class MemitSession {

    private final String id;
    private final List<Memo> queue;

    private MemitSession(String id){
        if(id == null){
            this.id = UUID.randomUUID().toString();
        }else{
            this.id = id;
        }
        this.queue = new ArrayList<Memo>(8);
    }

    public static MemitSession create(String id){
        return new MemitSession(id);
    }

    public static MemitSession create(){
        return new MemitSession(null);
    }

    public synchronized Memo take(){
        if( queue.isEmpty() ){
            return null;
        }
        return queue.remove(0);
    }

    public synchronized void add(Memo memo){
        queue.add(memo);
    }


    public String getSessionId(){
        return id;
    }

    @Override
    public String toString() {
        return "MemitSession{" +
                "id='" + id + '\'' +
                ", queue=" + queue +
                '}';
    }
}
