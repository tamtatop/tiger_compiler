package com.tiger;
//
//import com.tiger.antlr.TigerBaseVisitor;
//import com.tiger.antlr.TigerParser;
//import com.tiger.symbols.FunctionSymbol;
//import com.tiger.symbols.Symbol;
//import com.tiger.symbols.TypeSymbol;
//import com.tiger.types.*;
//
//import java.io.Writer;
//import java.util.*;
//
//import static java.lang.Integer.parseInt;
//
//class ParamListVisitor extends TigerBaseVisitor<List<Symbol>> {
//    @Override
//    public ArrayList<Symbol> visitParam_list(TigerParser.Param_listContext ctx) {
//        if (ctx.param() == null) {
//            return new ArrayList<>();
//        } else {
//            ArrayList<Symbol> params = visitParam_list_tail(ctx.param_list_tail());
//            params.add(parseParam(ctx.param()));
//            return params;
//        }
//    }
//
//    @Override
//    public ArrayList<Symbol> visitParam_list_tail(TigerParser.Param_list_tailContext ctx) {
//        if (ctx.param() == null) {
//            return new ArrayList<>();
//        } else {
//            ArrayList<Symbol> params = visitParam_list_tail(ctx.param_list_tail());
//            params.add(parseParam(ctx.param()));
//            return params;
//        }
//    }
//
//    public Symbol parseParam(TigerParser.ParamContext ctx) {
//        return new TypeSymbol(ctx.ID().getText(), new IntType());
//    }
//}
//
//class FunctionListVisitor {
//
//    public ArrayList<FunctionSymbol> visit_function_list(TigerParser.Funct_listContext ctx) {
//        if (ctx.funct() == null) {
//            return new ArrayList<>();
//        } else {
//            ArrayList<FunctionSymbol> functionSymbols = visit_function_list(ctx.funct_list());
//            functionSymbols.add(parseFunct(ctx.funct()));
//            return functionSymbols;
//        }
//    }
//
//    public FunctionSymbol parseFunct(TigerParser.FunctContext ctx) {
//        return new FunctionSymbol(ctx.ID().getText(), new ParamListVisitor().visitParam_list(ctx.param_list()), new IntType());
//    }
//
//}
//
//class SemanticVisitor extends TigerBaseVisitor<Void> {
//
//    SymbolTable symbolTable;
//
//    public SemanticVisitor(Writer symbolTableWriter) {
//        this.symbolTable = new SymbolTable(symbolTableWriter);
//    }
//
//    @Override
//    public Void visitTiger_program(TigerParser.Tiger_programContext ctx) {
//        System.out.println("visiting tiger_program");
//        System.out.printf("program name: %s%n", ctx.ID().getText());
//
//        symbolTable.createScope(); // create global scope
//        visitRootDeclaration_segment(ctx.declaration_segment());
//        visit(ctx.funct_list());
//        symbolTable.dropScope();
//
//        return null;
//    }
//
//
//    public void visitRootDeclaration_segment(TigerParser.Declaration_segmentContext ctx) {
//        System.out.println("visiting root declarations");
//        visit(ctx.type_declaration_list());
////        visit(ctx.var_declaration_list());
//    }
//
//    @Override
//    public Void visitType_declaration(TigerParser.Type_declarationContext ctx) {
//        symbolTable.insertSymbol(new TypeSymbol(ctx.ID().getText(), parseTypeDeclaration(ctx.type())));
//        return super.visitType_declaration(ctx);
//    }
//
//    public Type parseBaseType(TigerParser.Base_typeContext ctx) {
//        return switch (ctx.getText()) {
//            case "int" -> new IntType();
//            case "float" -> new FloatType();
//            default -> throw new IllegalStateException("Expected base_type got: " + ctx.getText());
//        };
//    }
//
//    public Type parseTypeDeclaration(TigerParser.TypeContext ctx) {
//        if (ctx.ARRAY() != null) {
//            return new ArrayType(parseInt(ctx.INTLIT().getText()), parseBaseType(ctx.base_type()));
//        }
//        if (ctx.ID() != null) {
//            return new CustomType(ctx.ID().getText());
//        }
//        return parseBaseType(ctx.base_type());
//// maybe possible?
////        return switch (ctx.getIndex()) {
////            case 0 -> parseBaseType(ctx.base_type());
////            case 1 -> new ArrayType(parseInt(ctx.INTLIT().getText()), parseBaseType(ctx.base_type()));
////            case 2 -> new CustomType(ctx.ID().getText());
////            default -> throw new IllegalStateException("Unexpected value: " + ctx.getRuleIndex());
////        };
//    }
//
//
//}


import com.tiger.antlr.TigerParser;
import org.antlr.v4.runtime.Token;

import java.io.Writer;

class SemanticChecker {
    SymbolTable symbolTable;

    public SemanticChecker(Writer symbolTableWriter) {
        this.symbolTable = new SymbolTable(symbolTableWriter);
    }

    public void visitTigerProgram(TigerParser.Tiger_programContext ctx) throws SemanticException {
        symbolTable.createScope();
        // DO WORK BITCH
        System.out.println(ctx.ID().getText());
        visitDeclarationSegment(ctx.declaration_segment(), true);

        symbolTable.dropScope();
    }

    public void visitDeclarationSegment(TigerParser.Declaration_segmentContext ctx, boolean isRoot) throws SemanticException {
        visitVarDeclarationList(ctx.var_declaration_list(), isRoot);

    }

    public void visitVarDeclarationList(TigerParser.Var_declaration_listContext ctx, boolean isRoot) throws SemanticException {
        visitVarDeclaration(ctx.var_declaration(), isRoot);
    }

    public void visitVarDeclaration(TigerParser.Var_declarationContext ctx, boolean isRoot) throws SemanticException {
        if (ctx.storage_class().STATIC() == null && !isRoot) {
            throw new SemanticException("var declaration is not allowed in root", ctx.storage_class().getStart());
        }
    }
}



























