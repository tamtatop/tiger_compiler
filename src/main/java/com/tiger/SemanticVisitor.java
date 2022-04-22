package com.tiger;

import com.tiger.antlr.TigerBaseVisitor;
import com.tiger.antlr.TigerParser;


class SemanticVisitor extends TigerBaseVisitor<Integer> {
    @Override
    public Integer visitTiger_program(TigerParser.Tiger_programContext ctx) {
        return super.visitTiger_program(ctx);
    }
}
