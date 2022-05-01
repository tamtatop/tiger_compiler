package com.tiger;

import com.tiger.io.CancellableWriter;
import com.tiger.symbols.Symbol;
import com.tiger.symbols.SymbolKind;
import com.tiger.symbols.VariableSymbol;
import com.tiger.types.BaseType;
import com.tiger.types.FloatType;
import com.tiger.types.IntType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;


class ScopeWithId {
    int scopeId;
    HashMap<String, Symbol> scope;

    public ScopeWithId(int scopeId, HashMap<String, Symbol> scope) {
        this.scopeId = scopeId;
        this.scope = scope;
    }
}

public class SymbolTable implements ISymbolTable {
    CancellableWriter writer;
    //    HashMap<String, Stack<Symbol>> symbolTable;
    Stack<ScopeWithId> symbolTable;
    int scopeIdCounter;
    int temporaryVariableCounter;

    public SymbolTable(CancellableWriter writer) {
        this.writer = writer;
        symbolTable = new Stack<>();
        scopeIdCounter = 0;
        temporaryVariableCounter = 0;
    }

    @Override
    public void insertSymbol(Symbol symbol) throws SymbolTableDuplicateKeyException {
        if (currentScopeContains(symbol.getName())) {
            throw new SymbolTableDuplicateKeyException(symbol.getName());
        }
        HashMap<String, Symbol> scope = symbolTable.peek().scope;
        scope.put(symbol.getName(), symbol);

        if(symbol.getSymbolKind() != SymbolKind.TEMP) {
            // do not write out temp variables
            writer.write(String.format("%s%s\n", "\t".repeat(symbolTable.size()), symbol.format()));
        }
    }

    public boolean currentScopeContains(String name) {
        return symbolTable.peek().scope.containsKey(name);
    }

    @Override
    public String curScopeName() {
        return String.valueOf(symbolTable.peek().scopeId);
    }

    @Override
    public Symbol getSymbol(String name) {
        for (ScopeWithId scope : symbolTable) {
            if (scope.scope.containsKey(name)) {
                return scope.scope.get(name);
            }
        }
        return null;
    }

    @Override
    public String getVariableScopeName(String name) {
        for (ScopeWithId scope : symbolTable) {
            if (scope.scope.containsKey(name)) {
                return String.valueOf(scope.scopeId);
            }
        }
        return null;
    }


    @Override
    public void createScope() {
        HashMap<String, Symbol> scope = new HashMap<>();
        symbolTable.push(new ScopeWithId(scopeIdCounter, scope));
        scopeIdCounter += 1;
        temporaryVariableCounter = 0;

        writer.write(String.format("%sscope %d:\n", "\t".repeat(symbolTable.size() - 1), scopeIdCounter));
    }

    @Override
    public void dropScope() {
        symbolTable.pop();
    }

    public List<NakedVariable> getNakedVariables() {
        HashMap<String, Symbol> scope = symbolTable.peek().scope;
        List<NakedVariable> nakedVars = new ArrayList<>();
        scope.forEach((name, symbol) -> {
            if (symbol.getSymbolKind() == SymbolKind.FUNCTION || symbol.getSymbolKind() == SymbolKind.TYPE) {
                return;
            }
            nakedVars.add(new NakedVariable(name, curScopeName(), symbol.getSymbolType().typeStructure()));
        });
        return nakedVars;
    }

    public String generateTemporary(BaseType baseType) {
        String name = String.format("tmp_%d", temporaryVariableCounter);
        try {
            switch (baseType) {
                case INT -> insertSymbol(new VariableSymbol(name, new IntType(), SymbolKind.TEMP));
                case FLOAT -> insertSymbol(new VariableSymbol(name, new FloatType(), SymbolKind.TEMP));
            }
        } catch (SymbolTableDuplicateKeyException e) {
            // should not happen
        }
        temporaryVariableCounter += 1;
        return name;
    }


    public NakedVariable getNaked(String name) {
        Symbol symbol = getSymbol(name);
        if (symbol == null) {
            return null;
        }
        if (symbol.getSymbolKind() == SymbolKind.FUNCTION || symbol.getSymbolKind() == SymbolKind.TYPE) {
            return null;
        }
        return new NakedVariable(name, getVariableScopeName(name), symbol.getSymbolType().typeStructure());
    }
}
