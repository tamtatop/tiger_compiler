package com.tiger.symbols;

public enum SymbolKind {
    TYPE,
    FUNCTION,
    VARIABLE,
    STATIC,
    PARAM,
    TEMP,
    ;

    public String format() {
        return switch (this) {
            case TYPE -> "type";
            case FUNCTION -> "function";
            case VARIABLE, PARAM -> "variable";
            case STATIC -> "static";
            case TEMP -> "temporary";
        };
    }
}
