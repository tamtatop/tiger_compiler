package com.tiger;

import com.tiger.io.CancellableWriter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

public class GraphVizGeneratorListener implements ParseTreeListener {

    private final CancellableWriter writer;
    private final Vocabulary vocabulary;
    private final String[] ruleNames;

    public GraphVizGeneratorListener(CancellableWriter writer, Vocabulary vocabulary, String[] ruleNames) {
        this.writer = writer;
        this.vocabulary = vocabulary;
        this.ruleNames = ruleNames;
    }

    @Override
    public void visitTerminal(TerminalNode node) {
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

    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        String symbolicName = ruleNames[ctx.getPayload().getRuleIndex()];
        String nodeId = symbolicName + ctx.getSourceInterval().hashCode();
        this.writer.write(nodeId + " [label=\"" + symbolicName + "\"];\n");

        if (ctx.getParent() == null) {
            return;
        }

        String parentSymbolicName = ruleNames[ctx.getParent().getPayload().getRuleIndex()];
        String parentNodeId = parentSymbolicName + ctx.getParent().getSourceInterval().hashCode();
        this.writer.write(parentNodeId + " -> " + nodeId + ";\n");

    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

    }
}
