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
    final int hits;

    public Memo(Cursor c){
        id = asString(c, "_id");
        question = asString(c, "question");
        answer = asString(c, "answer");
        questionLang = Lang.asLang( asString(c, "lang_question") );
        answerLang = Lang.asLang( asString(c, "lang_answer") );
        hits = asShort(c, "hits");
    }

    private Memo(Memo memo){
        id = memo.id;
        question = memo.question;
        answer = memo.answer;
        questionLang = memo.questionLang;
        answerLang = memo.answerLang;
        hits =  1 + memo.hits;
    }

    public Memo incrementHits(){
        return new Memo(this);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Memo memo = (Memo) o;
        return id.equals(memo.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
