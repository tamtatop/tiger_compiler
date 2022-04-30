package com.tiger;

public class Value {
    public NakedVariable variable;
    public NakedVariable array_idx; // null if not accessing anything

    public Value(NakedVariable variable) {
        this.variable = variable;
        this.array_idx = null;
    }

    public Value(NakedVariable variable, NakedVariable array_idx) {
        this.variable = variable;
        this.array_idx = array_idx;
    }
}
