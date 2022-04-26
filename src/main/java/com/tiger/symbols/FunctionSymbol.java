package com.tiger.symbols;

import com.tiger.types.Type;

import java.util.List;

public class FunctionSymbol implements Symbol {
    String name;
    List<Symbol> params;
    Type returnType;

    public FunctionSymbol(String name, List<Symbol> params, Type returnType) {
        this.name = name;
        this.params = params;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    @Override
    public SymbolKind getSymbolKind() {
        return SymbolKind.FUNCTION;
    }

    @Override
    public String format() {
        return null;
    }

}
