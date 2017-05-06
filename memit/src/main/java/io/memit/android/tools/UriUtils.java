package io.memit.android.tools;

import android.net.Uri;

import java.util.List;

/**
 * Created by peter on 2/20/17.
 */

public final class UriUtils {

    public static final String NOT_FOUND = null;
    private final static byte BOOK_ID_IDX = 1;
    private final static byte LECTURE_ID_IDX = 3;

    private UriUtils(){}

    public static int lastSegment(Uri uri){
        return Integer.valueOf(uri.getLastPathSegment());
    }

    public static String getBookId(Uri uri){
        return getValueAt(uri, BOOK_ID_IDX);
    }

    public static String getLectureId(Uri uri){
        return getValueAt(uri, LECTURE_ID_IDX);
    }

    public static String getLectureIdAsString(Uri uri){
        return  getLectureId(uri);
    }

    public static String getWordId(Uri uri){
        return uri.getLastPathSegment();
    }

    public static String getValueAt(Uri uri, int idx){
        List<String> paths = uri.getPathSegments();
        for(int i = 0; i < paths.size(); i++){
            if(i == idx){
                return paths.get(idx);
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

    public static Uri removeTwoLastSegment(Uri uri){
        return removeLastSegment(removeLastSegment(uri));
    }

    public static Uri withAppendedId(Uri contentUri, String id) {
        return appendId(contentUri.buildUpon(), id).build();
    }

    private static Uri.Builder appendId(Uri.Builder builder, String id) {
        return builder.appendEncodedPath(id);
    }

    public static String parseId(Uri contentUri) {
        return contentUri.getLastPathSegment();
    }
}
