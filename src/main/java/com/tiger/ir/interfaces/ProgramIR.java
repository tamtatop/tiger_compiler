package com.tiger.ir.interfaces;

import com.tiger.backend.BackendVariable;

import java.util.List;

public interface ProgramIR {
    String getProgramName();

    List<BackendVariable> getStaticVariables();

    BackendVariable getVariableByName(String name);

    List<FunctionIR> getFunctions();

    FunctionIR getFunctionByName(String name);
}
