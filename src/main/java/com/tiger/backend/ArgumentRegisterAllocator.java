package com.tiger.backend;

import com.tiger.types.BaseType;

import java.util.Stack;

public class ArgumentRegisterAllocator {
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
        if (intArgs.empty()) return null;
        else return intArgs.pop();
    }

    public String popFloat() {
        if (floatArgs.empty()) return null;
        else return floatArgs.pop();
    }

    public String popArgOfType(BaseType type) {
         return switch (type) {
            case INT -> popInt();
            case FLOAT -> popFloat();
        };
    }


}
