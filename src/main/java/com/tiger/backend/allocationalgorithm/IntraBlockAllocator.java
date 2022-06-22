package com.tiger.backend.allocationalgorithm;

import com.tiger.backend.BackendVariable;
import com.tiger.ir.interfaces.*;

import java.util.ArrayList;
import java.util.List;


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

                blockStart=blockEnd;
            }
        }

        return blocks;
    }

    public static List<BackendVariable> allocateVariablesInBlock(FunctionIR function, List<IRentry> entries) {

        // assign registers to frequently used vars in entries.

        return null;
    }
}
