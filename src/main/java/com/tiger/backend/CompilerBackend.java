package com.tiger.backend;

import com.tiger.backend.allocationalgorithm.NaiveRegisterAllocator;
import com.tiger.io.CancellableWriter;
import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.ir.interfaces.ProgramIR;

public class CompilerBackend {

    public static void runBackend(ProgramIR programIR, CancellableWriter cfgWriter, CancellableWriter livenessWriter, CancellableWriter mipsWriter) {
        new NaiveRegisterAllocator().runAllocationAlgorithm(programIR);
        MIPSGenerator mipsGenerator = new MIPSGenerator(mipsWriter);
        mipsGenerator.translateProgram(programIR);
    }

}
