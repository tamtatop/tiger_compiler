package com.tiger.symbols;

public interface Symbol {
    String getName();

    SymbolKind getSymbolKind();

    String format();
}
