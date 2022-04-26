package com.tiger;

import com.tiger.symbols.Symbol;

import java.io.Writer;
import java.util.HashMap;
import java.util.Stack;

class SymbolTable implements ISymbolTable {
    Writer writer;
    //    HashMap<String, Stack<Symbol>> symbolTable;
    Stack<HashMap<String, Symbol>> symbolTable;
    int cur_scope_id;

    public SymbolTable(Writer writer) {
        this.writer = writer;
        symbolTable = new Stack<>();
        cur_scope_id = 0;
    }

    @Override
    public void insertSymbol(Symbol symbol) {
        HashMap<String, Symbol> scope = symbolTable.peek();
        scope.put(symbol.getName(), symbol);
        System.out.printf("symbol inserted: %s\n", symbol.format());
    }

    @Override
    public Symbol getSymbol(String name) {
        for (HashMap<String, Symbol> scope : symbolTable) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    @Override
    public void createScope() {
        HashMap<String, Symbol> scope = new HashMap<>();
        symbolTable.push(scope);
        cur_scope_id += 1;
    }

    @Override
    public void dropScope() {
        symbolTable.pop();
        cur_scope_id -= 1;
    }
}
