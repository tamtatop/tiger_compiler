package com.tiger;

import com.tiger.io.CancellableWriter;
import com.tiger.symbols.Symbol;
import com.tiger.symbols.SymbolKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;


public class SymbolTable implements ISymbolTable {
    CancellableWriter writer;
    //    HashMap<String, Stack<Symbol>> symbolTable;
    Stack<HashMap<String, Symbol>> symbolTable;
    int cur_scope_id;

    public SymbolTable(CancellableWriter writer) {
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

        writer.write(String.format("%s%s\n", "\t".repeat(symbolTable.size()), symbol.format()));
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

        writer.write(String.format("%sscope %d:\n", "\t".repeat(symbolTable.size() - 1), cur_scope_id));
    }

    @Override
    public void dropScope() {
        symbolTable.pop();
    }

    public List<NakedVariable> getNakedVariables() {
        HashMap<String, Symbol> scope = symbolTable.peek();
        List<NakedVariable> nakedVars = new ArrayList<>();
        scope.forEach((name, symbol) -> {
            if (symbol.getSymbolKind() == SymbolKind.FUNCTION || symbol.getSymbolKind() == SymbolKind.TYPE) {
                return;
            }
            nakedVars.add(new NakedVariable(name, curScopeName(), symbol.getSymbolType().typeStructure()));
        });
        return nakedVars;
    }
}
