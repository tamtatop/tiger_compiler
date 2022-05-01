package com.tiger;

import com.tiger.antlr.TigerParser;
import com.tiger.io.CancellableWriter;
import com.tiger.symbols.*;
import com.tiger.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static java.lang.Integer.parseInt;

public class SemanticChecker {
    SymbolTable symbolTable;
    IrGenerator ir;
    SemanticErrorLogger errorLogger;


    public SemanticChecker(CancellableWriter symbolTableWriter, CancellableWriter irWriter, SemanticErrorLogger errorLogger) {
        this.symbolTable = new SymbolTable(symbolTableWriter);
        this.ir = new IrGenerator(irWriter);
        this.errorLogger = errorLogger;
    }

    public void visitTigerProgram(TigerParser.Tiger_programContext ctx) {
        symbolTable.createScope();
        // DO WORK BITCH
        System.out.printf("Hello program %s!\n", ctx.ID().getText());
        visitDeclarationSegment(ctx.declaration_segment(), true);
        ir.generateProgramHeader(ctx.ID().getText(), symbolTable.getNakedVariables());
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
            Type returnType = ctx.ret_type().type() == null ? null : parseType(ctx.ret_type().type());
            if (returnType != null && returnType.typeStructure().isArray()) {
                errorLogger.log(new SemanticException(String.format("function %s return type can't be array", ctx.ID().getText()), ctx.ret_type().start));
            }
            try {
                symbolTable.insertSymbol(new FunctionSymbol(ctx.ID().getText(), parseParamList(ctx.param_list()), returnType));
            } catch (SymbolTableDuplicateKeyException e) {
                errorLogger.log(new SemanticException("Name" + ctx.ID().getText() + "already exists", ctx.ID().getSymbol()));
            }
        } else {
            // actually recurse into body of the function here.
            symbolTable.createScope();
            FunctionSymbol funcSymbol = (FunctionSymbol) symbolTable.getSymbol(ctx.ID().getText());
            for (Symbol param : funcSymbol.params) {
                try {
                    symbolTable.insertSymbol(param);
                } catch (SymbolTableDuplicateKeyException e) {
                    // we've already reported duplicate variable error, so we can ignore it now
                }
            }
            ir.startFunction(funcSymbol);
            ir.setFunctionParams(symbolTable.getNakedVariables()); // bit too hacky
            visitStatSeq(ctx.stat_seq());
            ir.addVariablesFromScope(symbolTable.getNakedVariables());
            symbolTable.dropScope();
            ir.endFunction();
        }
    }

    public void visitStatSeq(TigerParser.Stat_seqContext ctx) {
        while (ctx != null) {
            visitStat(ctx.stat());
            ctx = ctx.stat_seq();
        }
    }

    public Value getValue(TigerParser.ValueContext ctx) {
        NakedVariable variable = symbolTable.getNaked(ctx.ID().getText());
        if(variable == null){
            errorLogger.log(new SemanticException(
                    String.format("variable %s not found", ctx.ID().getText()),
                    ctx.ID().getSymbol()
            ));
            return null;
        }
        NakedVariable idx = null;
        if (ctx.value_tail().expr() != null) {
            idx = generateExpr(ctx.value_tail().expr());
            if(idx == null){
                return null;
            }
            if(!variable.typeStructure.isArray()) {
                errorLogger.log(new SemanticException(
                        String.format("variable %s is not an array", ctx.ID().getText()),
                        ctx.ID().getSymbol()
                ));
                return null;
            }
        }
        return new Value(variable, idx);
    }

    public void visitStat(TigerParser.StatContext ctx) {
        if (ctx.LET() != null) {
            symbolTable.createScope();
            visitDeclarationSegment(ctx.declaration_segment(), false);
            visitStatSeq(ctx.stat_seq(0));
            ir.addVariablesFromScope(symbolTable.getNakedVariables());
            symbolTable.dropScope();
        }
        // value ASSIGN expr SEMICOLON
        if (ctx.value() != null) {
            // TODO: handle array assignment case.
            NakedVariable result = generateExpr(ctx.expr(0));
            if(result == null){ // ok?
                return;
            }
            Value lvalue = getValue(ctx.value());
            if(lvalue == null) {
                return;
            }
            if(lvalue.variable.typeStructure.base == BaseType.INT && result.typeStructure.base == BaseType.FLOAT) {
                errorLogger.log(new SemanticException("Can't promote float to int", ctx.value().start));
                return;
            }
            ir.emitAssign(lvalue, result);
        }
    }


    // let x=y;
    public NakedVariable generateExpr(TigerParser.ExprContext ctx) {
        // expr: value
        if (ctx.value() != null) {
            Value value = getValue(ctx.value());
            if(value.array_idx == null){
                return value.variable;
            } else {
                String tmpName = symbolTable.generateTemporary(value.variable.typeStructure.base);
                NakedVariable tmpVar = symbolTable.getNaked(tmpName);
                ir.emitArrayLoad(value.variable, value.array_idx, tmpVar);
                return tmpVar;
            }
        }

        // expr: OPENPAREN expr CLOSEPAREN;
        if (ctx.OPENPAREN() != null) {
            return generateExpr(ctx.expr(0));
        }

        // expr: numeric_const
        if (ctx.numeric_const() != null) {
            BaseType type = ctx.numeric_const().INTLIT() != null ? BaseType.INT : BaseType.FLOAT;
            String tmpName = symbolTable.generateTemporary(type);
            if(type == BaseType.INT) {
                ir.emitAssignImmediate(symbolTable.getNaked(tmpName), parseInt(ctx.numeric_const().getText()));
            } else {
                ir.emitAssignImmediate(symbolTable.getNaked(tmpName), Float.parseFloat(ctx.numeric_const().getText()));
            }
            return symbolTable.getNaked(tmpName);
        }
        // TODO: POW operation
        NakedVariable left = generateExpr(ctx.expr(0));
        NakedVariable right = generateExpr(ctx.expr(1));
        if(left == null || right == null){
            return null;
        }
        assert left.typeStructure.arraySize == 0;
        assert right.typeStructure.arraySize == 0;

        BaseType tmpType = null;

        // expr: <assoc=right> expr POW expr
        if(ctx.POW() != null){
            if(right.typeStructure.base != BaseType.INT){
                errorLogger.log(new SemanticException("The right operand for ** must be an integer", ctx.POW().getSymbol()));
                return null;
            }
            tmpType = BaseType.INT; // pow is always int
        }

        // expr: expr mult_div_operator expr
        // expr: expr plus_minus_operator expr
        if (ctx.mult_div_operator() != null || ctx.plus_minus_operator() != null) {
            // if left or right is float result is float.
            if(left.typeStructure.base == BaseType.FLOAT || right.typeStructure.base == BaseType.FLOAT){
                tmpType = BaseType.FLOAT;
            } else {
                tmpType = BaseType.INT;
            }
        }

        // expr: expr comparison_operator expr
        if (ctx.comparison_operator() != null) {
            if(left.typeStructure.base != right.typeStructure.base) {
                errorLogger.log(new SemanticException("Comparison operators take operands which may be either both integer or both float", ctx.comparison_operator().start));
                return null;
            }
            if(ctx.expr(0).comparison_operator() != null || ctx.expr(1).comparison_operator() != null){
                errorLogger.log(new SemanticException("Comparison operators do not associate, for example, a==b==c is a semantic error", ctx.comparison_operator().start));
                return null;
            }
            tmpType = BaseType.INT;
        }

        // expr: expr AND expr
        // expr: expr OR expr
        if (ctx.AND() != null || ctx.OR() != null) {
            tmpType = BaseType.INT;
        }

        assert tmpType != null;
        String tmpName = symbolTable.generateTemporary(tmpType);
        ir.emitBinaryOp(left,
                right,
                symbolTable.getNaked(tmpName),
                ctx.getChild(1).getText());

        return symbolTable.getNaked(tmpName);
    }

    public void visitDeclarationSegment(TigerParser.Declaration_segmentContext ctx, boolean isRoot) {
        visitTypeDeclarationList(ctx.type_declaration_list());
        visitVarDeclarationList(ctx.var_declaration_list(), isRoot);
    }

    public void visitVarDeclarationList(TigerParser.Var_declaration_listContext ctx, boolean isRoot) {
        if (ctx.var_declaration() == null) {
            return;
        }
        visitVarDeclaration(ctx.var_declaration(), isRoot);
        visitVarDeclarationList(ctx.var_declaration_list(), isRoot);
    }

    public void visitVarDeclaration(TigerParser.Var_declarationContext ctx, boolean isRoot) {
        if (ctx.storage_class().STATIC() == null && isRoot) {
            errorLogger.log(new SemanticException("var declaration is not allowed in global section", ctx.storage_class().getStart()));
        } else if (ctx.storage_class().STATIC() != null && !isRoot) {
            errorLogger.log(new SemanticException("static declaration is not allowed in local section", ctx.storage_class().getStart()));
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
            } catch (SymbolTableDuplicateKeyException e) {
                errorLogger.log(new SemanticException("Variable name" + name + "already exists in this scope", idCtx.ID().getSymbol()));
            }
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
        try {
            symbolTable.insertSymbol(new TypeSymbol(ctx.ID().getText(), parseType(ctx.type())));
        } catch (SymbolTableDuplicateKeyException e) {
            errorLogger.log(new SemanticException("Type" + ctx.ID().getText() + "already exists", ctx.ID().getSymbol()));
        }
    }

    public List<Symbol> parseParamList(TigerParser.Param_listContext ctx) {
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
                if (argNames.contains(name)) {
                    errorLogger.log(new SemanticException(String.format("duplicate parameter %s", name), cur.param().start));
                }
                argNames.add(name);
                params.add(symbol);

                cur = cur.param_list_tail();
            }
        }
        return params;
    }

    public Symbol parseParam(TigerParser.ParamContext ctx) {
        Type paramType = parseType(ctx.type());
        if (paramType.typeStructure().isArray()) {
            errorLogger.log(new SemanticException(String.format("parameter %s can't be array", ctx.ID().getText()), ctx.ID().getSymbol()));
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

    public Type parseType(TigerParser.TypeContext ctx) {
        if (ctx.ARRAY() != null) {
            return new ArrayType(parseInt(ctx.INTLIT().getText()), parseBaseType(ctx.base_type()));
        }
        if (ctx.ID() != null) {
            String typeName = ctx.ID().getText();
            Symbol symbol = symbolTable.getSymbol(typeName);
            if (symbol == null || symbol.getSymbolKind() != SymbolKind.TYPE) {
                errorLogger.log(new SemanticException(String.format("Type %s does not exist", typeName), ctx.ID().getSymbol()));
            }
            assert symbol != null;
            return new CustomType(typeName, symbol.getSymbolType().typeStructure());
        }
        return switch (parseBaseType(ctx.base_type())) {
            case INT -> new IntType();
            case FLOAT -> new FloatType();
        };
    }
}


