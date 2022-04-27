package com.tiger;

import com.tiger.antlr.TigerParser;
import com.tiger.symbols.*;
import com.tiger.types.*;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.lang.Integer.parseInt;

public class SemanticChecker {
    SymbolTable symbolTable;

    public SemanticChecker(Writer symbolTableWriter) {
        this.symbolTable = new SymbolTable(symbolTableWriter);
    }

    public void visitTigerProgram(TigerParser.Tiger_programContext ctx) throws SemanticException {
        symbolTable.createScope();
        // DO WORK BITCH
        System.out.printf("Hello program %s!\n", ctx.ID().getText());
        visitDeclarationSegment(ctx.declaration_segment(), true);
        visitFunctionList(ctx.funct_list(), false);
        visitFunctionList(ctx.funct_list(), true);
        symbolTable.dropScope();
    }

    /**
     * @param parseBody For first pass we only want to put function symbols in symbol tables.
     *                  We don't want to parse bodies of functions.
     */
    public void visitFunctionList(TigerParser.Funct_listContext ctx, boolean parseBody) throws SemanticException {
        if (ctx.funct() == null) {
            return;
        }
        visitFunction(ctx.funct(), parseBody);
        visitFunctionList(ctx.funct_list(), parseBody);
    }

    // funct: FUNCTION ID OPENPAREN param_list CLOSEPAREN ret_type BEGIN stat_seq END;

    /**
     * @param parseBody For first pass we only want to put function symbols in symbol tables.
     *                  We don't want to parse bodies of functions.
     */
    public void visitFunction(TigerParser.FunctContext ctx, boolean parseBody) throws SemanticException {
        if (!parseBody) {
            Type type = ctx.ret_type().type() == null ? null : parseType(ctx.ret_type().type());
            try {
                symbolTable.insertSymbol(new FunctionSymbol(ctx.ID().getText(), parseParamList(ctx.param_list()), type));
            } catch (SymbolTableDuplicateKeyException e){
                throw new SemanticException("Name" + ctx.ID().getText() + "already exists", ctx.ID().getSymbol());
            }
        } else {
            // actually recurse into body of the function here.
            symbolTable.createScope();
            FunctionSymbol funcSymbol = (FunctionSymbol)symbolTable.getSymbol(ctx.ID().getText());
            for (Symbol param: funcSymbol.params) {
                symbolTable.insertSymbol(param);
            }
            visitStatSeq(ctx.stat_seq());
            symbolTable.dropScope();
        }
    }

    public void visitStatSeq(TigerParser.Stat_seqContext ctx) throws SemanticException {
        while (ctx != null) {
            visitStat(ctx.stat());
            ctx = ctx.stat_seq();
        }
    }

    public void visitStat(TigerParser.StatContext ctx) throws SemanticException {
        if (ctx.LET() != null) {
            symbolTable.createScope();
            visitDeclarationSegment(ctx.declaration_segment(), false);
            visitStatSeq(ctx.stat_seq(0));
            symbolTable.dropScope();
        }
    }

    public void visitDeclarationSegment(TigerParser.Declaration_segmentContext ctx, boolean isRoot) throws SemanticException {
        visitTypeDeclarationList(ctx.type_declaration_list());
        visitVarDeclarationList(ctx.var_declaration_list(), isRoot);
    }

    public void visitVarDeclarationList(TigerParser.Var_declaration_listContext ctx, boolean isRoot) throws SemanticException {
        if (ctx.var_declaration() == null) {
            return;
        }
        visitVarDeclaration(ctx.var_declaration(), isRoot);
        visitVarDeclarationList(ctx.var_declaration_list(), isRoot);
    }

    public void visitVarDeclaration(TigerParser.Var_declarationContext ctx, boolean isRoot) throws SemanticException {
        if (ctx.storage_class().STATIC() == null && isRoot) {
            throw new SemanticException("var declaration is not allowed in global section", ctx.storage_class().getStart());
        } else if (ctx.storage_class().STATIC() != null && !isRoot) {
            throw new SemanticException("static declaration is not allowed in local section", ctx.storage_class().getStart());
        }
        SymbolKind symbolKind;
        if (ctx.storage_class().STATIC() != null) {
            symbolKind = SymbolKind.STATIC;
        } else {
            symbolKind = SymbolKind.VARIABLE;
        }
        Type symbolType = parseType(ctx.type());

        // id_list: ID | ID COMMA id_list;
        TigerParser.Id_listContext idCtx = ctx.id_list();
        while (idCtx != null) {
            String name = idCtx.ID().getText();
            try {
                symbolTable.insertSymbol(new VariableSymbol(name, symbolType, symbolKind));
            } catch (SymbolTableDuplicateKeyException e){
                throw new SemanticException("Variable name" + name + "already exists in this scope", idCtx.ID().getSymbol());
            }
            idCtx = idCtx.id_list();
        }
    }

    public void visitTypeDeclarationList(TigerParser.Type_declaration_listContext ctx) throws SemanticException {
        if (ctx.type_declaration() == null) {
            return;
        }
        visitTypeDeclaration(ctx.type_declaration());
        visitTypeDeclarationList(ctx.type_declaration_list());
    }

    public void visitTypeDeclaration(TigerParser.Type_declarationContext ctx) throws SemanticException {
        try {
            symbolTable.insertSymbol(new TypeSymbol(ctx.ID().getText(), parseType(ctx.type())));
        } catch (SymbolTableDuplicateKeyException e) {
            throw new SemanticException("Type" + ctx.ID().getText() + "already exists", ctx.ID().getSymbol());
        }
    }

    public List<Symbol> parseParamList(TigerParser.Param_listContext ctx) throws SemanticException {
        ArrayList<Symbol> params = new ArrayList<>();
        HashSet<String> argNames = new HashSet<>();
        if (ctx.param() != null) {

            Symbol symbol = parseParam(ctx.param());
            String name = symbol.getName();
            argNames.add(name);
            params.add(symbol);

            TigerParser.Param_list_tailContext cur = ctx.param_list_tail();
            while (cur.param() != null) {

                symbol = parseParam(cur.param());
                name = symbol.getName();
                if(argNames.contains(name)){
                    throw new SemanticException(String.format("duplicate parameter %s", name), cur.param().start);
                }
                argNames.add(name);
                params.add(symbol);

                cur = cur.param_list_tail();
            }
        }
        return params;
    }

    public Symbol parseParam(TigerParser.ParamContext ctx) throws SemanticException {
        Type paramType = parseType(ctx.type());
        if (paramType.typeStructure().isArray()) {
            throw new SemanticException(String.format("parameter %s can't be array", ctx.ID().getText()), ctx.ID().getSymbol());
        }
        return new VariableSymbol(ctx.ID().getText(), paramType, SymbolKind.PARAM);
    }

    public BaseType parseBaseType(TigerParser.Base_typeContext ctx) {
        return switch (ctx.getText()) {
            case "int" -> BaseType.INT;
            case "float" -> BaseType.FLOAT;
            default -> throw new IllegalStateException("Expected base_type got: " + ctx.getText());
        };
    }

    public Type parseType(TigerParser.TypeContext ctx) throws SemanticException {
        if (ctx.ARRAY() != null) {
            return new ArrayType(parseInt(ctx.INTLIT().getText()), parseBaseType(ctx.base_type()));
        }
        if (ctx.ID() != null) {
            String typeName = ctx.ID().getText();
            Symbol symbol = symbolTable.getSymbol(typeName);
            if(symbol == null || symbol.getSymbolKind() != SymbolKind.TYPE){
                throw new SemanticException(String.format("Type %s does not exist",typeName), ctx.ID().getSymbol());
            }
            return new CustomType(typeName, symbol.getSymbolType().typeStructure());
        }
        return switch (parseBaseType(ctx.base_type())) {
            case INT -> new IntType();
            case FLOAT -> new FloatType();
        };
    }
}


