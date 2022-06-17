package com.tiger.ir;

import com.tiger.NakedVariable;
import com.tiger.types.BaseType;

import java.util.List;

public interface IrGeneratorListener {
    void genFunction(String functionName, List<NakedVariable> localVariables, List<NakedVariable> arguments, String irBody, BaseType base);

    void genProgram(String programName, List<NakedVariable> staticVariables);
}
