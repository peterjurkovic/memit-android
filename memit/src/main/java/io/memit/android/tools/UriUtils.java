package io.memit.android.tools;

import android.net.Uri;

import java.util.List;

/**
 * Created by peter on 2/20/17.
 */

public final class UriUtils {

    public static final byte NOT_FOUND = -1;
    private final static byte BOOK_ID_IDX = 1;
    private final static byte LECTURE_ID_IDX = 3;

    private UriUtils(){}


    public static long getBookId(Uri uri){
        return getValueAt(uri, BOOK_ID_IDX);
    }

    public static long getLectureId(Uri uri){
        return getValueAt(uri, LECTURE_ID_IDX);
    }

    public static long getWordId(Uri uri){
        return Long.valueOf( uri.getLastPathSegment());
    }

    public static long getValueAt(Uri uri, int idx){
        List<String> paths = uri.getPathSegments();
        for(int i = 0; i < paths.size(); i++){
            if(i == idx){
                String id = paths.get(idx);
                if(id.matches("\\d+")){
                    return Long.valueOf(id);
                }
            }
        }
        return NOT_FOUND;
    }

    public static Uri removeLastSegment(Uri uri){
        final List<String> segments = uri.getPathSegments();
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme(uri.getScheme());
        builder.encodedAuthority(uri.getAuthority());
        builder.encodedQuery(uri.getQuery());
        for (int i = 0; i < segments.size() -1; i++) {
            builder.appendPath(segments.get(i));
        }
        return builder.build();
    }

}
