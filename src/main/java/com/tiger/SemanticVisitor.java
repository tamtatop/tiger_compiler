package com.tiger;

import com.tiger.antlr.TigerBaseVisitor;
import com.tiger.antlr.TigerParser;
import com.tiger.symbols.Symbol;

import java.io.Writer;
import java.util.*;


interface ISymbolTable {
    void insertSymbol(Symbol symbol);
    Symbol getSymbol(String name);
    void createScope();
    void dropScope();
}


//
//

// func, static, var, type

class SymbolTable implements ISymbolTable{
    Writer writer;
//    HashMap<String, Stack<Symbol>> symbolTable;
    Stack<HashMap<String, Symbol>> symbolTable;
    int cur_scope_id;

    public SymbolTable(Writer writer){
        this.writer = writer;
        symbolTable = new Stack<>();
        cur_scope_id = 0;
    }

    @Override
    public void insertSymbol(Symbol symbol) {
        HashMap<String, Symbol> scope = symbolTable.peek();
        scope.put(symbol.getName(), symbol);
    }

    @Override
    public Symbol getSymbol(String name) {
        for (HashMap<String, Symbol> scope: symbolTable) {
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



class SemanticVisitor extends TigerBaseVisitor<Void> {

    SymbolTable symbolTable;


    @Override
    public Void visitTiger_program(TigerParser.Tiger_programContext ctx) {
        System.out.println("visiting tiger_program");
        System.out.printf("program name: %s!%n", ctx.ID().getText());
        visitRootDeclaration_segment(ctx.declaration_segment());
        visit(ctx.funct_list());

        // TODO: code we want to be able to write
//        Symbol symbol = symbolTable.getSymbol(name);
//        symbol.getName();
//        symbol.toString();
//        symbolTable = new SymbolTable(writer);
//        symbolTable.insertSymbol(symbol);
//        symbolTable.createScope();
//        symbolTable.dropScope();

        return null;
    }

    @Override
    public Void visitDeclaration_segment(TigerParser.Declaration_segmentContext ctx) {

        System.out.println("visiting declarations");

        return visitChildren(ctx);
    }


//    funct: FUNCTION ID OPENPAREN param_list CLOSEPAREN ret_type BEGIN stat_seq END;
    @Override
    public Void visitFunct(TigerParser.FunctContext ctx) {

        //FunctionSymbol fsymbol = FunctionSymbol(ctx.ID().getText(), ctx.ret_type().type())
        symbolTable.createScope();
        Void res = super.visitFunct(ctx);
        symbolTable.dropScope();
        return res;
    }

    public void visitRootDeclaration_segment(TigerParser.Declaration_segmentContext ctx) {
        System.out.println("visiting root declarations");
        visitChildren(ctx);
    }

}
