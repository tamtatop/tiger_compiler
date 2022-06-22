package com.tiger.backend.allocationalgorithm;

import com.tiger.ir.interfaces.*;

import java.util.List;


public class IntraBlockAllocator {


    public static List<IRBlock> findBlocks(FunctionIR f) {
        List<IRentry> body = f.getBody();
        boolean[] leaders = new boolean[body.size()];
        leaders[0] = true;
        for (int i = 0; i < body.size(); i++) {
            if (body.get(i).isLabel()) {
                leaders[i] = true;
            } else {
                IRInstruction instr = body.get(i).asInstruction();
                if (instr.getType() == IRInstructionType.BRANCH || instr.getType() == IRInstructionType.GOTO || instr.getType() == IRInstructionType.RETURN) {
                    if (i + 1 < body.size()) {
                        leaders[i + 1] = true;
                    }
                }
            }
        }
        int blockStart = 0;
        for (int i = 1; i < body.size(); i++) {
            if (leaders[i]) {
                int blockEnd = i; // exclusive
                // block at: [blockStart, blockEnd)


            }
        }
        return List.of();
    }

    public static void allocateVariablesInBlock(FunctionIR function, List<IRentry> entries) {

        // assign registers to frequently used vars in entries.

    }
}
