package com.tiger.backend;

import com.tiger.types.BaseType;

import java.util.Stack;

class ArgumentRegisterAllocator {
    private final static String[] INT_ARGS = {"a0", "a1", "a2", "a3"};
    private final static String[] FLOAT_ARGS = {"f12", "f14"};

    private final Stack<String> intArgs = new Stack<>();
    private final Stack<String> floatArgs = new Stack<>();

    public ArgumentRegisterAllocator() {
        for (String intTemp : INT_ARGS) {
            intArgs.push(intTemp);
        }
        for (String floatTemp : FLOAT_ARGS) {
            floatArgs.push(floatTemp);
        }
    }

    public String popInt() {
        return intArgs.pop();
    }

    public String popFloat() {
        return floatArgs.pop();
    }

    public String popTempOfType(BaseType type) {
        return switch (type) {
            case INT -> intArgs.pop();
            case FLOAT -> floatArgs.pop();
        };
    }


}
