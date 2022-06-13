package com.tiger.ir.interfaces;

import java.util.List;

public interface IRInstruction {
    List<String> reads();

    List<String> writes();

    String getIthCode(int i);

    IRInstructionType getType();
}
