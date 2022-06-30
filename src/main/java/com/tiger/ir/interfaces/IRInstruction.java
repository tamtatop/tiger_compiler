package com.tiger.ir.interfaces;

import java.util.List;

public interface IRInstruction {
    List<String> reads();

    List<String> writes();

    int getLoopDepth();

    int size();

    String getIthCode(int i);

    IRInstructionType getType();
}
