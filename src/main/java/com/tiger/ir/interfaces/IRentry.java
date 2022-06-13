package com.tiger.ir.interfaces;

public interface IRentry {
    boolean isLabel();

    boolean isInstruction();

    IRInstruction asInstruction();

    IRLabel asLabel();
}
