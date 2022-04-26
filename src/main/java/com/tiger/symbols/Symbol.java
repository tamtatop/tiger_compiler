package com.tiger.symbols;

import com.tiger.types.Type;

public interface Symbol {
    String getName();

    SymbolKind getSymbolKind();

    Type getSymbolType();

    default String format() {
        return String.format("%s, %s, %s", getName(), getSymbolKind().format(), getSymbolType().format());
    }
}