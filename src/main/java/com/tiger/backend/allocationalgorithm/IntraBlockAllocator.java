package com.tiger.backend.allocationalgorithm;

import com.tiger.backend.BackendVariable;
import com.tiger.ir.interfaces.*;
import com.tiger.types.BaseType;

import java.util.*;

class VarUsage implements Comparable<VarUsage> {
    String name;
    Integer freq;


    public VarUsage(String name, Integer freq) {
        this.name = name;
        this.freq = freq;
    }

    @Override
    public int compareTo(VarUsage o) {
        return o.freq.compareTo(freq);
    }
}

class SaveRegisterAllocator {

    private final static  String[] INT_SAVES = {"$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7"};
    private final static String[] FLOAT_SAVES = {"$f20", "$f21", "$f22", "$f23", "$f24", "$f25", "$f26", "$f27", "$f28", "$f29", "$f30"};
    private final Stack<String> intSaves = new Stack<>();
    private final Stack<String> floatSaves = new Stack<>();

    public SaveRegisterAllocator() {
        for (String intTemp : INT_SAVES) {
            intSaves.push(intTemp);
        }
        for (String floatTemp : FLOAT_SAVES) {
            floatSaves.push(floatTemp);
        }
    }

    public String popInt() {
        if (intSaves.isEmpty()) {return null;}
        return intSaves.pop();
    }

    public String popFloat() {
        if (floatSaves.isEmpty()) {return null;}
        return floatSaves.pop();
    }

    public String popTempOfType(BaseType type) {
        return switch (type) {
            case INT -> intSaves.pop();
            case FLOAT -> floatSaves.pop();
        };
    }
}


public class IntraBlockAllocator {


    public static List<IRBlock> findBlocks(FunctionIR f) {
        List<IRentry> body = f.getBody();
        boolean[] leaders = new boolean[body.size()];
        leaders[0] = true; // Rule i
        for (int i = 0; i < body.size(); i++) {
            if (body.get(i).isLabel()) {
                leaders[i] = true; // Rule ii (all labels are used as targets in our IR)
            } else {
                IRInstruction instr = body.get(i).asInstruction();
                if (instr.getType() == IRInstructionType.BRANCH || instr.getType() == IRInstructionType.GOTO || instr.getType() == IRInstructionType.RETURN) {
                    if (i + 1 < body.size()) {
                        leaders[i + 1] = true; // Rule iii
                    }
                }
            }
        }
        ArrayList<IRBlock> blocks = new ArrayList<>();
        int blockStart = 0;
        int blockIdx = 0;
        for (int i = 1; i < body.size(); i++) {
            if (leaders[i]) {
                int blockEnd = i; // exclusive
                // new block at: [blockStart, blockEnd)

                blocks.add(new IRBlock(blockIdx, body.subList(blockStart, blockEnd)));

                blockStart = blockEnd;
            }
        }

        return blocks;
    }

    public static List<BackendVariable> allocateVariablesInBlock(FunctionIR function, List<IRentry> blockEntries) {

        // assign registers to frequently used vars in entries.
        SaveRegisterAllocator saveRegisterAllocator = new SaveRegisterAllocator();

        HashMap<String, Integer> varUsageFrequencies = new HashMap<>();
        for (IRentry entry : blockEntries) {
            if (entry.isInstruction()) {
                IRInstruction instr = entry.asInstruction();
                fillVarUsageFrequencies(varUsageFrequencies, instr.reads());
                fillVarUsageFrequencies(varUsageFrequencies, instr.writes());
            }
        }

        return varUsageFrequencies
                .entrySet()
                .stream()
                .map(e -> new VarUsage(e.getKey(), e.getValue()))
                .sorted()
                .map(var -> {
                    BackendVariable varData = function.fetchVariableByName(var.name);
                    String register = saveRegisterAllocator.popTempOfType(varData.typeStructure.base);
                    if (register != null) {
                        varData.assignRegister(register);
                    } else {
                        varData.spill();
                    }
                    return varData;
                })
                .toList();
    }

    private static void fillVarUsageFrequencies(HashMap<String, Integer> varUsageFrequencies, List<String> usedVars) {
        for (String var : usedVars) {
            if (varUsageFrequencies.containsKey(var)) {
                varUsageFrequencies.put(var, varUsageFrequencies.get(var) + 1);
            } else {
                varUsageFrequencies.put(var, 1);
            }
        }
    }
}
