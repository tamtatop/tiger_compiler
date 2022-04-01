package com.tiger;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.BufferedWriter;
import java.io.IOException;

public class GraphVizGeneratorListener implements ParseTreeListener {

    private final BufferedWriter writer;
    private final Vocabulary vocabulary;
    private final String[] ruleNames;

    public GraphVizGeneratorListener(BufferedWriter writer, Vocabulary vocabulary, String[] ruleNames) {
        this.writer = writer;
        this.vocabulary = vocabulary;
        this.ruleNames = ruleNames;
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        try {
            String symbolicName = vocabulary.getSymbolicName(node.getSymbol().getType());
            String nodeId = symbolicName + node.getSourceInterval().hashCode();
            String displayName = symbolicName + ":'" + node.getText() + "'";
            this.writer.write(nodeId + " [label=\"" + displayName + "\"];\n");

            if (node.getParent() == null) {
                return;
            }

            String parentSymbolicName = ruleNames[((RuleContext) node.getParent().getPayload()).getRuleIndex()];
            String parentNodeId = parentSymbolicName + node.getParent().getSourceInterval().hashCode();
            this.writer.write(parentNodeId + " -> " + nodeId + ";\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        try {
            String symbolicName = ruleNames[ctx.getPayload().getRuleIndex()];
            String nodeId = symbolicName + ctx.getSourceInterval().hashCode();
            this.writer.write(nodeId + " [label=\"" + symbolicName + "\"];\n");

            if (ctx.getParent() == null) {
                return;
            }

            String parentSymbolicName = ruleNames[((RuleContext) ctx.getParent().getPayload()).getRuleIndex()];
            String parentNodeId = parentSymbolicName + ctx.getParent().getSourceInterval().hashCode();
            this.writer.write(parentNodeId + " -> " + nodeId + ";\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

    }
}
