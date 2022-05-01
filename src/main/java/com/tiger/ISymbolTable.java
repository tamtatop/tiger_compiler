package com.tiger;

import com.tiger.symbols.Symbol;



class SymbolTableDuplicateKeyException extends Exception {
    public SymbolTableDuplicateKeyException(String msg) {
        super(msg);
    }
}


interface ISymbolTable {
    void insertSymbol(Symbol symbol) throws SymbolTableDuplicateKeyException;

    String curScopeName();

    Symbol getSymbol(String name);

    String getVariableScopeName(String name);

    void createScope();

    void dropScope();
}
