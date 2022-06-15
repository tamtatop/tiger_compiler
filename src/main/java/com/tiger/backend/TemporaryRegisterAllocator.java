package com.tiger.backend;

import com.tiger.types.BaseType;

import java.util.Stack;

public class TemporaryRegisterAllocator {

    private final static String[] INT_TEMPS = {"t0", "t1", "t2", "t3"};
    private final static String[] FLOAT_TEMPS = {"t0", "t1", "t2", "t3"};
    private final Stack<String> itemps = new Stack<>();
    private final Stack<String> ftemps = new Stack<>();

    public TemporaryRegisterAllocator() {
        for (String intTemp : INT_TEMPS) {
            itemps.push(intTemp);
        }
        for (String floatTemp : FLOAT_TEMPS) {
            ftemps.push(floatTemp);
        }
    }

    public String popInt() {
        return itemps.pop();
    }

    public String popFloat() {
        return ftemps.pop();
    }

    public String popTempOfType(BaseType type) {
        return switch (type) {
            case INT -> itemps.pop();
            case FLOAT -> ftemps.pop();
        };
    }
}
