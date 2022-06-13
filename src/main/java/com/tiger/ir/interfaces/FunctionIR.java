package com.tiger.ir.interfaces;

import com.tiger.NakedVariable;

import java.util.List;

public interface FunctionIR {
    String getFunctionName();

    List<NakedVariable> getLocalVariables();

    List<NakedVariable> getArguments();

    List<IRentry> getBody();
}
