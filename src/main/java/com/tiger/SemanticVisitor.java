package com.tiger;

import com.tiger.antlr.TigerBaseVisitor;
import com.tiger.antlr.TigerParser;

import java.util.ArrayList;
import java.util.HashMap;


class SemanticVisitor extends TigerBaseVisitor<Void> {

    HashMap<String, ArrayList<Void>> symbolTable;


    @Override
    public Void visitTiger_program(TigerParser.Tiger_programContext ctx) {
        System.out.println("visiting tiger_program");
        System.out.printf("program name: %s!%n", ctx.ID().getText());
        visitRootDeclaration_segment(ctx.declaration_segment());
        visit(ctx.funct_list());

        return null;
    }

    @Override
    public Void visitDeclaration_segment(TigerParser.Declaration_segmentContext ctx) {
        System.out.println("visiting declarations");
        return visitChildren(ctx);
    }

    public void visitRootDeclaration_segment(TigerParser.Declaration_segmentContext ctx) {
        System.out.println("visiting root declarations");
        visitChildren(ctx);
    }

}
