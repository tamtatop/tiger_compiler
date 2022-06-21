package com.tiger.backend.allocationalgorithm;

import com.tiger.backend.BackendVariable;
import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.ir.interfaces.ProgramIR;

public class NaiveRegisterAllocator implements RegisterAllocator {

    @Override
    public void runAllocationAlgorithm(ProgramIR ir) {
        for (FunctionIR functionIR : ir.getFunctions()) {
            for (BackendVariable localVariable : functionIR.getLocalVariables()) {
                localVariable.spill();
            }
        }
    }
}
