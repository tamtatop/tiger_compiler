package com.tiger.backend.allocationalgorithm;

import com.tiger.backend.BackendVariable;
import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.ir.interfaces.ProgramIR;

public class NaiveRegisterAllocator {

    public static void runAllocationAlgorithm(FunctionIR function) {
        for (BackendVariable localVariable : function.getLocalVariables()) {
            localVariable.spill();
        }
    }
}
