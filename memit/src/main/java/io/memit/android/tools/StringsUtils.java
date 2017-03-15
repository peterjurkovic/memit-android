package io.memit.android.tools;

import java.util.Collection;

/**
 * Created by peter on 3/14/17.
 */

public final class StringsUtils {

    private StringsUtils(){}


    public static String join(Collection<? extends  Object> list, String joiner){
        if(list == null){
            return null;
        }
        int len = list.size();
        int i = 0;
        StringBuilder str = new StringBuilder();
        for(Object obj : list){
            if(i++ > 0 && i <= len ){
                str.append(joiner);
            }
            str.append(obj);
        }
        return str.toString();
    }


    public static String joinConditions(Collection<? extends  Object> list, String column, String orAnd){
        if(list == null){
            return null;
        }
        int len = list.size();
        int i = 0;
        StringBuilder str = new StringBuilder();
        for(Object obj : list){
            if(i++ > 0 && i <= len ){
                str.append(" ").append(orAnd).append(" ");
            }
            str.append(column).append("=").append(obj);
        }
        return str.toString();
    }

}