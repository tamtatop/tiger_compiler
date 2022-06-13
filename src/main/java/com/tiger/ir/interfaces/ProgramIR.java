package com.tiger.ir.interfaces;

import com.tiger.NakedVariable;
import com.tiger.ir.interfaces.FunctionIR;

import java.util.List;

public interface ProgramIR {
    String getProgramName();

    List<NakedVariable> getStaticVariables();

    List<FunctionIR> getFunctions();

    FunctionIR getFunctionByName(String name);
}
