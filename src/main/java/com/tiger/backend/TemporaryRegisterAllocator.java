package com.tiger.backend;

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

    String popi() {
        return itemps.pop();
    }

    String popf() {
        return ftemps.pop();
    }
}
