package com.tiger.symbols;

public enum SymbolKind {
    TYPE,
    FUNCTION,
    VARIABLE,
    STATIC,
    PARAM, // TODO: Does this need to be separate from VARIABLE?
    TEMP, // TODO: Do not print these in `.st` file
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
