package com.tiger.ir.interfaces;

import com.tiger.BackendVariable;
import com.tiger.NakedVariable;
import com.tiger.ir.interfaces.FunctionIR;

import java.util.List;

public interface ProgramIR {
    String getProgramName();

    List<BackendVariable> getStaticVariables();

    List<FunctionIR> getFunctions();

    FunctionIR getFunctionByName(String name);
}
