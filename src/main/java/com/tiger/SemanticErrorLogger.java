package com.tiger;

public class SemanticErrorLogger {
    private boolean anyError;

    void log(SemanticException e) {
        anyError = true;
        System.err.printf("line %d:%d %s\n", e.line_number, e.column_number, e.message);
    }

    boolean anyError() {
        return anyError;
    }
}
