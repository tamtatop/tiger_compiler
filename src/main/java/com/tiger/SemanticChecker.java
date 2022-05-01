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

class IrGenerator {
    CancellableWriter writer;
    private int labelCounter;

    public IrGenerator(CancellableWriter writer) {
        this.writer = writer;
        this.labelCounter = 0;
    }

    private String mangledName(NakedVariable v) {
        if (v.scopeName == null) {
            return "_" + v.name;
        } else {
            return "_" + v.scopeName + "_" + v.name;
        }
    }

    private String generateSpecificTypeVariablesNames(List<NakedVariable> variables, BaseType desiredType) {
        StringBuilder names = new StringBuilder();
        for (NakedVariable variable : variables) {
            if (variable.typeStructure.base == desiredType) {
                if (variable.typeStructure.isArray()) {
                    names.append(String.format(" %s[%d],",
                            mangledName(variable),
                            variable.typeStructure.arraySize));
                } else {
                    names.append(String.format(" %s,", mangledName(variable)));
                }
            }
        }
        if (names.isEmpty()) {
            return names.toString();
        } else {
            return names.substring(0, names.length() - 1);
        }
    }


    private void generateVariableLists(String prefix, List<NakedVariable> variables) {
        writer.write(prefix + "int-list:");
        writer.write(generateSpecificTypeVariablesNames(variables, BaseType.INT));
        writer.write("\n");
        writer.write(prefix + "float-list: ");
        writer.write(generateSpecificTypeVariablesNames(variables, BaseType.FLOAT));
        writer.write("\n");
    }

    public void generateProgramHeader(String programName, List<NakedVariable> variables) {
        writer.write(String.format("start_program %s\n", programName));
        generateVariableLists("static-", variables);
    }


    public void emitAssign(Value target, NakedVariable source) {
        if (target.array_idx == null) {
            writer.write(String.format("assign, %s, %s,\n", mangledName(target.variable), mangledName(source)));
        } else {
            // TODO: implement array assignement
        }
    }

    // Word immediate means same as numeric constant
    public void emitAssignImmediate(NakedVariable target, Integer imm) {
        // FIXME: implement same for floats
        writer.write(String.format("assign, %s, %d,\n", mangledName(target), imm));
    }

    private static final HashMap<String, String> opToIrOp = new HashMap<>();
    private static final HashMap<String, String> cmpOp = new HashMap<>();

    static {
        opToIrOp.put("+", "add");
        opToIrOp.put("-", "sub");
        opToIrOp.put("*", "mult");
        opToIrOp.put("/", "div");
        opToIrOp.put("&", "and");
        opToIrOp.put("|", "or");

        cmpOp.put("==", "breq");
        cmpOp.put("!=", "brneq");
        cmpOp.put("<", "brlt");
        cmpOp.put(">", "brgt");
        cmpOp.put("<=", "brleq");
        cmpOp.put(">=", "brgeq");
    }

    public void emitBinaryOp(NakedVariable left, NakedVariable right, NakedVariable target, String op) {
        if (opToIrOp.containsKey(op)) {
            writer.write(String.format("%s, %s, %s, %s\n", opToIrOp.get(op), mangledName(left), mangledName(right), mangledName(target)));
        }
        if(cmpOp.containsKey(op)){
            // target = 1
            // breq left, right, skip:
            // target = 0
            // skip:
            // ...
            String skipLabel = newUniqueLabel("cmp-op");
            emitAssignImmediate(target, 1);
            writer.write(String.format("%s, %s, %s, %s\n", cmpOp.get(op), mangledName(left), mangledName(right), skipLabel));
            emitAssignImmediate(target, 0);
            emitLabel(skipLabel);
        }
    }

    public String newUniqueLabel(String prefix) {
        labelCounter+=1;
        return String.format("_%s-%d", prefix, labelCounter);
    }

    public void emitLabel(String label) {
        writer.write(String.format("%s:\n", label));
    }

//
//    public startFunction() {
//
//    }
//
//    public generateExprCode() {
//
//    }
//
//    public addVariablesFromScope() {
//
//    }
//
//    public String generateTemporaryVariable(){
//        currentFunctionVariables.add(random);
//        return random;
//    }
//
//    public flushFunction() {
//
//    }

}

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
                    // unreachable coz it's already checked
                    // FIXME: it's reachable now since we are not exiting on error
                }
            }
            visitStatSeq(ctx.stat_seq());
            symbolTable.dropScope();
        }
    }

    public void visitStatSeq(TigerParser.Stat_seqContext ctx) {
        while (ctx != null) {
            visitStat(ctx.stat());
            ctx = ctx.stat_seq();
        }
    }

    // TODO: create new struct Value or something instead of NakedVariable
    public Value getValue(TigerParser.ValueContext ctx) {
        // TODO: Handle case where symbol does not exist
        NakedVariable variable = symbolTable.getNaked(ctx.ID().getText());
        NakedVariable idx = null;
        if (ctx.value_tail().expr() != null) {
            idx = generateExpr(ctx.value_tail().expr());
        }
        // TODO: if idx!=null check that variable is actually an array
        return new Value(variable, idx);
    }

    public void visitStat(TigerParser.StatContext ctx) {
        if (ctx.LET() != null) {
            symbolTable.createScope();
            visitDeclarationSegment(ctx.declaration_segment(), false);
            visitStatSeq(ctx.stat_seq(0));
            symbolTable.dropScope();
        }
        // value ASSIGN expr SEMICOLON
        if (ctx.value() != null) {
            NakedVariable variable = generateExpr(ctx.expr(0));
            if(variable == null){ // ok?
                return;
            }
            Value lvalue = getValue(ctx.value());
            if(lvalue == null) {
                return;
            }
            ir.emitAssign(lvalue, variable);
        }
    }


    // let x=y;
    public NakedVariable generateExpr(TigerParser.ExprContext ctx) {
        // expr: value
        if (ctx.value() != null) {
            return getValue(ctx.value()).variable; // FIXME: wrong for array value
        }

        // expr: OPENPAREN expr CLOSEPAREN;
        if (ctx.OPENPAREN() != null) {
            return generateExpr(ctx.expr(0));
        }

        // expr: numeric_const
        if (ctx.numeric_const() != null) {
            // FIXME: support floats
            String tmpName = symbolTable.generateTemporary(BaseType.INT);
            ir.emitAssignImmediate(symbolTable.getNaked(tmpName), parseInt(ctx.numeric_const().getText()));
            return symbolTable.getNaked(tmpName);
        }
        // TODO: Type check, POW operation, DRY code, a==b==c should be an error
        NakedVariable left = generateExpr(ctx.expr(0));
        NakedVariable right = generateExpr(ctx.expr(0));
        // TODO: we can stop evaluating on errors right?
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
                return null; // TODO: we can stop evaluating right?
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
            return new CustomType(typeName, symbol.getSymbolType().typeStructure());
        }
        return switch (parseBaseType(ctx.base_type())) {
            case INT -> new IntType();
            case FLOAT -> new FloatType();
        };
    }
}


