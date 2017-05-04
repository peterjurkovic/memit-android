package io.memit.android.activity.memit;

import android.database.Cursor;

import io.memit.android.model.Lang;
import io.memit.android.tools.CursorUtils;

import static io.memit.android.tools.CursorUtils.asShort;
import static io.memit.android.tools.CursorUtils.asString;

/**
 * Created by peter on 4/27/17.
 */

public class Memo {

    final String id;
    final String question;
    final String answer;
    final Lang questionLang;
    final Lang answerLang;
    final short hits;
    final short cardsLeft;

    public Memo(Cursor c){
        id = asString(c, "_id");
        question = asString(c, "question");
        answer = asString(c, "answer");
        questionLang = Lang.asLang( asString(c, "lang_question") );
        answerLang = Lang.asLang( asString(c, "lang_answer") );
        hits = asShort(c, "hits");
    }

    public static Memo map(Cursor cursor){
        return new Memo(cursor);
    }

    @Override
    public String toString() {
        return "Memo{" +
                "id='" + id + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", questionLang=" + questionLang +
                ", answerLang=" + answerLang +
                ", hits=" + hits +
                '}';
    }
}
