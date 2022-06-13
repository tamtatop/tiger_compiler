package com.tiger.ir.interfaces;

import com.tiger.BackendVariable;

import java.util.List;

public interface FunctionIR {
    String getFunctionName();

    List<BackendVariable> getLocalVariables();

    List<BackendVariable> getArguments();

    List<IRentry> getBody();
}
