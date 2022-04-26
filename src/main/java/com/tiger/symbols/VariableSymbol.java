package com.tiger.symbols;

import com.tiger.types.Type;

public class VariableSymbol implements Symbol {
    String name;
    Type variableType;
    SymbolKind kind;

    public VariableSymbol(String name, Type variableType, SymbolKind kind) {
        this.name = name;
        this.variableType = variableType;
        this.kind = kind;
        assert kind == SymbolKind.STATIC || kind == SymbolKind.VARIABLE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SymbolKind getSymbolKind() {
        return kind;
    }

    @Override
    public Type getSymbolType() {
        return variableType;
    }
}
