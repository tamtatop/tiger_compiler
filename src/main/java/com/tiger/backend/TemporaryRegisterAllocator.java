package com.tiger.backend;

import com.tiger.types.BaseType;

import java.util.Stack;

public class TemporaryRegisterAllocator {

    private final static String[] INT_TEMPS = {"$t0", "$t1", "$t2", "$t3"};
    private final static String[] FLOAT_TEMPS = {"$f4", "$f5", "$f6", "$f7"};
    private final Stack<String> intTemps = new Stack<>();
    private final Stack<String> floatTemps = new Stack<>();

    public TemporaryRegisterAllocator() {
        for (String intTemp : INT_TEMPS) {
            intTemps.push(intTemp);
        }
        for (String floatTemp : FLOAT_TEMPS) {
            floatTemps.push(floatTemp);
        }
    }

    public String popInt() {
        return intTemps.pop();
    }

    public String popFloat() {
        return floatTemps.pop();
    }

    public String popTempOfType(BaseType type) {
        return switch (type) {
            case INT -> intTemps.pop();
            case FLOAT -> floatTemps.pop();
        };
    }
}
