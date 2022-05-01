package com.tiger.symbols;

import com.tiger.types.Type;

public interface Symbol {
    String getName();

    SymbolKind getSymbolKind();

    Type getSymbolType();


    default String format() {
        if (getSymbolType() == null)
            return String.format("%s, %s, %s", getName(), getSymbolKind().format(), "void");
        else
            return String.format("%s, %s, %s", getName(), getSymbolKind().format(), getSymbolType().format());
    }
}