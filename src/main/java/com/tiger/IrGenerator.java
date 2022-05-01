package com.tiger;

import com.tiger.io.CancellableWriter;
import com.tiger.symbols.FunctionSymbol;
import com.tiger.symbols.Symbol;
import com.tiger.types.BaseType;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class IrGenerator {
    private final CancellableWriter writer;

    // current function that we are emitting
    private FunctionSymbol curFunction;
    // IR code for current function
    private StringWriter funcIr;
    private final StringWriter staticIr;
    // common scope for whole function
    private ArrayList<NakedVariable> funcVariables;
    private ArrayList<NakedVariable> funcParams;

    private int labelCounter;

    public IrGenerator(CancellableWriter writer) {
        this.writer = writer;
        this.labelCounter = 0;
        this.staticIr = new StringWriter(0);
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
        writer.write("\t"+prefix + "int-list:");
        writer.write(generateSpecificTypeVariablesNames(variables, BaseType.INT));
        writer.write("\n");
        writer.write("\t"+prefix + "float-list: ");
        writer.write(generateSpecificTypeVariablesNames(variables, BaseType.FLOAT));
        writer.write("\n");
    }

    public void generateProgramHeader(String programName, List<NakedVariable> variables) {
        writer.write(String.format("start_program %s\n", programName));
        generateVariableLists("static-", variables);
        writer.write("\n");
    }


    public void emitAssign(Value target, NakedVariable source) {
        if (target.array_idx == null) {
            funcIr.write(String.format("assign, %s, %s,\n", mangledName(target.variable), mangledName(source)));
        } else {
            funcIr.write(String.format("array_store, %s, %s, %s\n", mangledName(target.variable), mangledName(target.array_idx), mangledName(source)));
        }
    }

    // Word immediate means same as numeric constant
    public void emitAssignImmediate(NakedVariable target, Integer imm) {
        if(target.typeStructure.isArray()) {
            Objects.requireNonNullElse(funcIr, staticIr).write(String.format("assign, %s, %d, %d\n", mangledName(target), target.typeStructure.arraySize, imm));
        } else {
            Objects.requireNonNullElse(funcIr, staticIr).write(String.format("assign, %s, %d,\n", mangledName(target), imm));
        }
    }

    public void emitAssignImmediate(NakedVariable target, Float imm) {
        if(target.typeStructure.isArray()) {
            Objects.requireNonNullElse(funcIr, staticIr).write(String.format("assign, %s, %d, %f\n", mangledName(target), target.typeStructure.arraySize, imm));
        } else {
            Objects.requireNonNullElse(funcIr, staticIr).write(String.format("assign, %s, %f,\n", mangledName(target), imm));
        }
    }

    public void emitGoto(String label) {
        funcIr.write(String.format("goto, %s, ,\n", label));
    }

    public void emitIfCondition(NakedVariable variable, String elseLabel) {
        funcIr.write(String.format("brneq, %s, 0, %s\n", mangledName(variable), elseLabel));
    }

    public void emitForCondition(NakedVariable variable1, NakedVariable variable2, String afterForLabel) {
        funcIr.write(String.format("brgeq, %s, %s, %s\n", mangledName(variable1), mangledName(variable2), afterForLabel));
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

    public void emitArrayLoad(NakedVariable arr, NakedVariable idx, NakedVariable target) {
        funcIr.write(String.format("array_load, %s, %s, %s\n", mangledName(target), mangledName(arr), mangledName(idx)));
    }

    public void emitBinaryOp(NakedVariable left, NakedVariable right, NakedVariable target, String op) {
        if (opToIrOp.containsKey(op)) {
            funcIr.write(String.format("%s, %s, %s, %s\n", opToIrOp.get(op), mangledName(left), mangledName(right), mangledName(target)));
        }
        if (cmpOp.containsKey(op)) {
            // target = 1
            // breq left, right, skip:
            // target = 0
            // skip:
            // ...
            String skipLabel = newUniqueLabel("cmp_op");
            emitAssignImmediate(target, 1);
            funcIr.write(String.format("%s, %s, %s, %s\n", cmpOp.get(op), mangledName(left), mangledName(right), skipLabel));
            emitAssignImmediate(target, 0);
            emitLabel(skipLabel);
        }
    }

    // i = i+1
    public void emitVariableIncrement(NakedVariable var){
        funcIr.write(String.format("add, %s, 1, %s\n", mangledName(var), mangledName(var)));
    }

    public String newUniqueLabel(String prefix) {
        labelCounter += 1;
        return String.format("_%s_%d", prefix, labelCounter);
    }

    public void emitLabel(String label) {
        funcIr.write(String.format("%s:\n", label));
    }


    public void startFunction(FunctionSymbol curFunction, String scopeName) {
        this.curFunction = curFunction;
        funcIr = new StringWriter(0);
        funcVariables = new ArrayList<>();
        funcParams = new ArrayList<>();
        for (Symbol param : curFunction.params) {
            funcParams.add(new NakedVariable(param.getName(), scopeName, param.getSymbolType().typeStructure()));
        }
    }

    public void addVariablesFromScope(List<NakedVariable> variables) {
        funcVariables.addAll(variables);
    }

    private String generateSignature(List<NakedVariable> params) {
        StringBuilder signature = new StringBuilder();
        for (NakedVariable variable : params) {
            assert !variable.typeStructure.isArray();
            signature.append(String.format("%s %s, ",
                    variable.typeStructure.base.format(),
                    mangledName(variable)));
        }
        if (signature.isEmpty()) {
            return signature.toString();
        } else {
            return signature.substring(0, signature.length() - 2);
        }
    }


    public void endFunction() {
        writer.write(String.format("start_function %s\n", curFunction.name));

        String retType;
        if(curFunction.returnType == null){
            retType = "void";
        } else {
            retType = curFunction.returnType.format();
        }
        writer.write(String.format("\t%s %s(%s)\n", retType, curFunction.name, generateSignature(funcParams)));

        generateVariableLists("", funcVariables);
        writer.write(String.format("\t%s:\n", curFunction.name));

        writer.write("\t");
        if(curFunction.name.equals("main")){
            writer.write(staticIr.toString().replace("\n", "\n\t"));
        }
        writer.write(funcIr.toString().replace("\n", "\n\t"));
        if(curFunction.returnType == null)
            writer.write("return , , , \n");
        else
            writer.write("return , , , \n");

        writer.write(String.format("end_function %s\n\n", curFunction.name));
    }

    public void emitReturn(NakedVariable retVal) {
        if(retVal == null){
            funcIr.write("return , , ,");
        } else {
            funcIr.write(String.format("return, %s, ,", mangledName(retVal)));
        }
    }
    private String callArgs(ArrayList<NakedVariable> args) {
        StringBuilder argsir = new StringBuilder();
        for (NakedVariable arg : args) {
            argsir.append(String.format("%s, ", mangledName(arg)));
        }
        if (argsir.isEmpty()) {
            return argsir.toString();
        } else {
            return argsir.substring(0, argsir.length() - 2);
        }
    }

    public void emitCall(FunctionSymbol func, ArrayList<NakedVariable> args) {
        funcIr.write(String.format("call %s, %s\n",  func.name, callArgs(args)));
    }
    public void emitCallR(FunctionSymbol func, ArrayList<NakedVariable> args, NakedVariable target) {
        funcIr.write(String.format("call %s, %s, %s\n", mangledName(target), func.name, callArgs(args)));
    }

}
