package io.memit.android.provider;

/**
 * Created by peter on 1/30/17.
 */

public class LectureDatabaseOperation extends BaseDatabaseOperations {

    private static final Object lock = new Object();
    private static LectureDatabaseOperation operations;


    private LectureDatabaseOperation( final DatabaseOpenHelper helper){
        super(helper);
    }

    public static LectureDatabaseOperation getInstance(final DatabaseOpenHelper helper){
        if(operations == null){
            synchronized (lock){
                if(operations == null){
                    operations = new LectureDatabaseOperation(helper);
                }
            }
        }
        return operations;
    }

}
