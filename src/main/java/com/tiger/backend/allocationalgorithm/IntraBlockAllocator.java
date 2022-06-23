package com.tiger.backend.allocationalgorithm;

import com.tiger.backend.BackendVariable;
import com.tiger.ir.interfaces.*;

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


public class IntraBlockAllocator {


    public static List<IRBlock> findBlocks(FunctionIR f) {
        List<IRentry> body = f.getBody();
        boolean[] leaders = new boolean[body.size()];
        leaders[0] = true; // Rule i
        int[] blockId = new int[body.size()];
        int curBlock = 0;
        HashMap<String, Integer> labelInstructionIndex = new HashMap<>();
        for (int i = 0; i < body.size(); i++) {
            if (body.get(i).isLabel()) {
                leaders[i] = true; // Rule ii (all labels are used as targets in our IR)
                labelInstructionIndex.put(body.get(i).asLabel().getName(), i);
            } else {
                IRInstruction instr = body.get(i).asInstruction();
                if (instr.getType() == IRInstructionType.BRANCH || instr.getType() == IRInstructionType.GOTO || instr.getType() == IRInstructionType.RETURN) {
                    if (i + 1 < body.size()) {
                        leaders[i + 1] = true; // Rule iii
                    }
                }
            }
            if(leaders[i]) {
                curBlock+=1;
                blockId[i]=curBlock;
            }
        }
        ArrayList<IRBlock> blocks = new ArrayList<>();
        int blockStart = 0;
        for (int i = 1; i < body.size(); i++) {
            if (leaders[i]) {
                int blockEnd = i; // exclusive
                // new block at: [blockStart, blockEnd)

                blocks.add(new IRBlock(blockId[blockStart], body.subList(blockStart, blockEnd), f.getFunctionName()));

                blockStart = blockEnd;
            }
        }
        int blockEnd = body.size();
        blocks.add(new IRBlock(blockId[blockStart], body.subList(blockStart, blockEnd), f.getFunctionName()));

        for (int i = 0; i < body.size(); i++) {
            if (body.get(i).isInstruction()) {
                IRInstruction instr = body.get(i).asInstruction();
                String target = null;
                if (instr.getType() == IRInstructionType.BRANCH) {
                    target = instr.getIthCode(3);
                }
                if (instr.getType() == IRInstructionType.GOTO) {
                    target = instr.getIthCode(1);
                }
                if(target != null) {
                    blocks.get(blockId[i]).neighbours
                            .add(blocks.get(blockId[labelInstructionIndex.get(target)]));
                }
            }
            if(i>0&&leaders[i]){
                if(body.get(i-1).isInstruction()
                        && body.get(i-1).asInstruction().getType() != IRInstructionType.RETURN
                        && body.get(i-1).asInstruction().getType() != IRInstructionType.GOTO) {
                    blocks.get(blockId[i-1]).neighbours
                            .add(blocks.get(blockId[i]));
                }
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
                .map(var -> function.fetchVariableByName(var.name))
                .filter(varData -> !varData.isStatic)
                .peek(varData -> {
                    varData.resetAllocation();
                    if(varData.typeStructure.isArray()) {
                        varData.spill();
                    } else {
                        String register = saveRegisterAllocator.popTempOfType(varData.typeStructure.base);
                        if (register != null) {
                            varData.assignRegister(register);
                        } else {
                            varData.spill();
                        }
                    }
                })
                .filter(varData -> !varData.isSpilled)
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
