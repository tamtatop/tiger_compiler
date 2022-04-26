package com.tiger;

import com.tiger.antlr.TigerBaseVisitor;
import com.tiger.antlr.TigerParser;
import com.tiger.symbols.Symbol;
import com.tiger.symbols.TypeSymbol;
import com.tiger.types.*;

import java.io.Writer;
import java.util.*;

import static java.lang.Integer.parseInt;


interface ISymbolTable {
    void insertSymbol(Symbol symbol);

    Symbol getSymbol(String name);

    void createScope();

    void dropScope();
}



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

class SemanticVisitor extends TigerBaseVisitor<Void> {

    SymbolTable symbolTable;

    public SemanticVisitor(Writer symbolTableWriter) {
        this.symbolTable = new SymbolTable(symbolTableWriter);
    }

    @Override
    public Void visitTiger_program(TigerParser.Tiger_programContext ctx) {
        System.out.println("visiting tiger_program");
        System.out.printf("program name: %s%n", ctx.ID().getText());

        symbolTable.createScope(); // create global scope
        visitRootDeclaration_segment(ctx.declaration_segment());
        visit(ctx.funct_list());
        symbolTable.dropScope();

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


    public void visitRootDeclaration_segment(TigerParser.Declaration_segmentContext ctx) {
        System.out.println("visiting root declarations");
        visit(ctx.type_declaration_list());
//        visit(ctx.var_declaration_list());
    }

    @Override
    public Void visitType_declaration(TigerParser.Type_declarationContext ctx) {
        symbolTable.insertSymbol(new TypeSymbol(ctx.ID().getText(), parseTypeDeclaration(ctx.type())));
        return super.visitType_declaration(ctx);
    }

    public Type parseBaseType(TigerParser.Base_typeContext ctx) {
        return switch (ctx.getText()) {
            case "int" -> new IntType();
            case "float" -> new FloatType();
            default -> throw new IllegalStateException("Expected base_type got: " + ctx.getText());
        };
    }

    public Type parseTypeDeclaration(TigerParser.TypeContext ctx) {
        if (ctx.ARRAY() != null) {
            return new ArrayType(parseInt(ctx.INTLIT().getText()), parseBaseType(ctx.base_type()));
        }
        if (ctx.ID() != null) {
            return new CustomType(ctx.ID().getText());
        }
        return parseBaseType(ctx.base_type());
// maybe possible?
//        return switch (ctx.getIndex()) {
//            case 0 -> parseBaseType(ctx.base_type());
//            case 1 -> new ArrayType(parseInt(ctx.INTLIT().getText()), parseBaseType(ctx.base_type()));
//            case 2 -> new CustomType(ctx.ID().getText());
//            default -> throw new IllegalStateException("Unexpected value: " + ctx.getRuleIndex());
//        };
    }


}
