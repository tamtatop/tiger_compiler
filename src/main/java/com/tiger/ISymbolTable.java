package com.tiger;

import com.tiger.symbols.Symbol;

interface ISymbolTable {
    void insertSymbol(Symbol symbol);

    Symbol getSymbol(String name);

    void createScope();

    void dropScope();
}
