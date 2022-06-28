package com.tiger.backend;

import com.tiger.backend.allocationalgorithm.NaiveRegisterAllocator;
import com.tiger.backend.allocationalgorithm.RegisterAllocationAlgorithm;
import com.tiger.io.CancellableWriter;
import com.tiger.ir.interfaces.ProgramIR;

public class CompilerBackend {

    public static void runBackend(ProgramIR programIR, CancellableWriter cfgWriter, CancellableWriter livenessWriter, CancellableWriter mipsWriter, RegisterAllocationAlgorithm algorithm) {
        MIPSGenerator mipsGenerator = new MIPSGenerator(mipsWriter, cfgWriter, livenessWriter);
        mipsGenerator.translateProgram(programIR, algorithm);
    }

}
