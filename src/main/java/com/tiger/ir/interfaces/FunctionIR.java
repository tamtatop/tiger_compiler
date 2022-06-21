package com.tiger.ir.interfaces;

import com.tiger.backend.BackendVariable;
import com.tiger.types.BaseType;

import java.util.List;

public interface FunctionIR {
    String getFunctionName();

    List<BackendVariable> getLocalVariables();

    List<BackendVariable> getArguments();

    BaseType getReturnType();

    List<IRentry> getBody();

    BackendVariable fetchVariableByName(String name);
}
