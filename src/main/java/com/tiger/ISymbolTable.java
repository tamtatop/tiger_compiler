package com.tiger;

import com.tiger.symbols.Symbol;

import javax.management.openmbean.KeyAlreadyExistsException;


class SymbolTableDuplicateKeyException extends KeyAlreadyExistsException {
    public SymbolTableDuplicateKeyException(String msg) {
        super(msg);
    }
}

interface ISymbolTable {
    void insertSymbol(Symbol symbol) throws SymbolTableDuplicateKeyException;

    String curScopeName();

    Symbol getSymbol(String name);

    void createScope();

    void dropScope();
}
