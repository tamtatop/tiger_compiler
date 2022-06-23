package com.tiger.backend.allocationalgorithm;

import com.tiger.types.BaseType;

import java.util.Stack;

public class SaveRegisterAllocator {

    public final static String[] INT_SAVES = {"$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7"};
    public final static String[] FLOAT_SAVES = {"$f20", "$f21", "$f22", "$f23"};
    private final Stack<String> intSaves = new Stack<>();
    private final Stack<String> floatSaves = new Stack<>();

    public SaveRegisterAllocator() {
        for (String intTemp : INT_SAVES) {
            intSaves.push(intTemp);
        }
        for (String floatTemp : FLOAT_SAVES) {
            floatSaves.push(floatTemp);
        }
    }

    public String popInt() {
        if (intSaves.isEmpty()) {
            return null;
        }
        return intSaves.pop();
    }

    public String popFloat() {
        if (floatSaves.isEmpty()) {
            return null;
        }
        return floatSaves.pop();
    }

    public String popTempOfType(BaseType type) {
        return switch (type) {
            case INT -> popInt();
            case FLOAT -> popFloat();
        };
    }
}
