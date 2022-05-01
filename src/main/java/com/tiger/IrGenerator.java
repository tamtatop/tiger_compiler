package com.tiger;

import com.tiger.io.CancellableWriter;
import com.tiger.types.BaseType;

import java.util.HashMap;
import java.util.List;

public class IrGenerator {
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
        writer.write(String.format("assign, %s, %d,\n", mangledName(target), imm));
    }

    public void emitAssignImmediate(NakedVariable target, Float imm) {
        writer.write(String.format("assign, %s, %f,\n", mangledName(target), imm));
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
        writer.write(String.format("array_load, %s, %s, %s\n", mangledName(target), mangledName(arr), mangledName(idx)));
    }

    public void emitBinaryOp(NakedVariable left, NakedVariable right, NakedVariable target, String op) {
        if (opToIrOp.containsKey(op)) {
            writer.write(String.format("%s, %s, %s, %s\n", opToIrOp.get(op), mangledName(left), mangledName(right), mangledName(target)));
        }
        if (cmpOp.containsKey(op)) {
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
        labelCounter += 1;
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
