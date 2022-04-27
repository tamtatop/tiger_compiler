package com.tiger;

import com.tiger.symbols.Symbol;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Stack;


public class SymbolTable implements ISymbolTable {
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
    public void insertSymbol(Symbol symbol) throws SymbolTableDuplicateKeyException {
        if (currentScopeContains(symbol.getName())) {
            throw new SymbolTableDuplicateKeyException(symbol.getName());
        }
        HashMap<String, Symbol> scope = symbolTable.peek();
        scope.put(symbol.getName(), symbol);

        try {
            writer.write(String.format("%s%s\n", "\t".repeat(symbolTable.size()), symbol.format()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean currentScopeContains(String name) {
        return symbolTable.peek().containsKey(name);
    }

    @Override
    public String curScopeName() {
        return String.valueOf(cur_scope_id);
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

        try {
            writer.write(String.format("%sscope %d:\n", "\t".repeat(symbolTable.size() - 1), cur_scope_id));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dropScope() {
        symbolTable.pop();
    }
}
