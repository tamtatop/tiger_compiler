package com.tiger;

import com.tiger.antlr.TigerParser;
import com.tiger.symbols.*;
import com.tiger.types.*;

import java.io.Writer;
import java.util.ArrayList;
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
    public void visitFunctionList(TigerParser.Funct_listContext ctx, boolean parseBody) {
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
    public void visitFunction(TigerParser.FunctContext ctx, boolean parseBody) {
        if (!parseBody) {
            Type type = ctx.ret_type().type() == null ? null : parseType(ctx.ret_type().type());
            symbolTable.insertSymbol(new FunctionSymbol(ctx.ID().getText(), parseParamList(ctx.param_list()), type));
        } else {
            // actually recurse into body of the function here.
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
            symbolTable.insertSymbol(new VariableSymbol(name, symbolType, symbolKind));
            idCtx = idCtx.id_list();
        }
    }

    public void visitTypeDeclarationList(TigerParser.Type_declaration_listContext ctx) {
        if (ctx.type_declaration() == null) {
            return;
        }
        visitTypeDeclaration(ctx.type_declaration());
        visitTypeDeclarationList(ctx.type_declaration_list());
    }

    public void visitTypeDeclaration(TigerParser.Type_declarationContext ctx) {
        symbolTable.insertSymbol(new TypeSymbol(ctx.ID().getText(), parseType(ctx.type())));
    }

    public List<Symbol> parseParamList(TigerParser.Param_listContext ctx) {
        ArrayList<Symbol> params = new ArrayList<>();
        if (ctx.param() != null) {
            params.add(parseParam(ctx.param()));
            TigerParser.Param_list_tailContext cur = ctx.param_list_tail();
            while (cur.param() != null) {
                params.add(parseParam(ctx.param()));
                cur = cur.param_list_tail();
            }
        }
        return params;
    }

    public Symbol parseParam(TigerParser.ParamContext ctx) {
        return new VariableSymbol(ctx.ID().getText(), parseType(ctx.type()), SymbolKind.PARAM);
    }


    public Type parseBaseType(TigerParser.Base_typeContext ctx) {
        return switch (ctx.getText()) {
            case "int" -> new IntType();
            case "float" -> new FloatType();
            default -> throw new IllegalStateException("Expected base_type got: " + ctx.getText());
        };
    }

    public Type parseType(TigerParser.TypeContext ctx) {
        if (ctx.ARRAY() != null) {
            return new ArrayType(parseInt(ctx.INTLIT().getText()), parseBaseType(ctx.base_type()));
        }
        if (ctx.ID() != null) {
            return new CustomType(ctx.ID().getText());
        }
        return parseBaseType(ctx.base_type());
    }
}


