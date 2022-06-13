package com.tiger.ir.interfaces;

public enum IRInstructionType {
    ASSIGN, // could be array assignment!!!
    BINOP,
    GOTO,
    BRANCH,
    RETURN,
    CALL,
    ARRAYSTORE,
    ARRAYLOAD,
}
