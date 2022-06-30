package com.tiger.backend;

import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.ir.interfaces.IRInstruction;
import com.tiger.ir.interfaces.IRentry;

public class SpillCostCalculator {
    private static final int readCost = 10;
    private static final int writeCost = 10;
    private static final int loopMultiplier = 10;

    public static void calculateVariableSpillCosts(FunctionIR functionIR) {
        for (IRentry iRentry : functionIR.getBody()) {
            if (iRentry.isInstruction()) {
                IRInstruction irInstruction = iRentry.asInstruction();
                for (String readVar : irInstruction.reads()) {
                    functionIR.fetchVariableByName(readVar).spillCost += readCost * Math.pow(loopMultiplier, irInstruction.getLoopDepth());
                }
                for (String writeVar : irInstruction.writes()) {
                    functionIR.fetchVariableByName(writeVar).spillCost += writeCost * Math.pow(loopMultiplier, irInstruction.getLoopDepth());
                }
            }
        }
    }
}
