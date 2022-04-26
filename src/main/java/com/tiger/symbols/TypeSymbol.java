package com.tiger.symbols;

import com.tiger.types.Type;

public class TypeSymbol implements Symbol {
    String name;
    Type type;

    public TypeSymbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SymbolKind getSymbolKind() {
        return SymbolKind.TYPE;
    }

    @Override
    public Type getSymbolType() {
        return type;
    }
}
