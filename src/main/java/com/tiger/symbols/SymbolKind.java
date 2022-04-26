package com.tiger.symbols;

public enum SymbolKind {
    TYPE,
    FUNCTION,
    VARIABLE,
    STATIC,
    ;

    public String format() {
        return switch (this) {
            case TYPE -> "type";
            case FUNCTION -> "function";
            case VARIABLE -> "variable";
            case STATIC -> "static";
        };
    }
}
