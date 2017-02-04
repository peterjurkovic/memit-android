package io.memit.android.tools;

/**
 * Created by peter on 2/4/17.
 */

public class State {

    final String id;
    final String label;

    State(String id, String label){
        this.id = id;
        this.label =  label == null ? id : label;
    }

    public String getId() {
        return id;
    }
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return id.equals(state.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
