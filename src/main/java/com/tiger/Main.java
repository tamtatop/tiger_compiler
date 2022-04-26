package com.tiger;

import com.tiger.antlr.TigerLexer;
import com.tiger.antlr.TigerParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;


public class Main {

    public static void generateTokens(TigerLexer tigerLexer, Writer lexerWriter) {
        Vocabulary vocabulary = tigerLexer.getVocabulary();
        List<? extends Token> allTokens = tigerLexer.getAllTokens();

        try {
            BufferedWriter writer = new BufferedWriter(lexerWriter);

            for (Token token : allTokens) {
                String symbolicName = vocabulary.getSymbolicName(token.getType());
                String text = token.getText();
                writer.write("<" + symbolicName + ", " + "\"" + text + "\"" + ">\n");
            }

            writer.close();
        } catch (IOException e) {
            System.err.println("Could not create lexer output file");
            System.exit(1);
        }
    }


    private static void generateGraph(TigerLexer tigerLexer, TigerParser parser, Writer parserWriter) {
        try {
            BufferedWriter writer = new BufferedWriter(parserWriter);

            ParseTree tree = parser.tiger_program();

            writer.write("digraph G {\n");
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(new GraphVizGeneratorListener(writer, tigerLexer.getVocabulary(), parser.getRuleNames()), tree);
            writer.write("}\n");
            writer.close();
        } catch (IOException e) {
            System.err.println("Could not create parser output file");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        TigerArgs tigerArgs = new TigerArgs(args);


        CharStream charStream = IOUtils.charStreamFromFilename(tigerArgs.inputFilename);
        Writer lexerWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.lexerFilename);
        Writer parserWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.parserFilename);
        Writer symbolTableWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.symbolTableFilename);
        Writer irWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.irFilename);

        TigerLexer lexer = new TigerLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new FailingErrorListener("Lexer error", 2));

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        TigerParser parser = new TigerParser(tokens);
        parser.addErrorListener(new FailingErrorListener("Parser error", 3));

        generateTokens(lexer, lexerWriter);
        lexer.reset();
        generateGraph(lexer, parser, parserWriter);

        // Experiments
        lexer.reset();
        parser.reset();
        SemanticChecker semanticChecker = new SemanticChecker(symbolTableWriter);
        try {
            semanticChecker.visitTigerProgram(parser.tiger_program());
        } catch (SemanticException e) {
            System.err.printf("line %d:%d %s", e.line_number, e.column_number, e.message);
        }

    }


}
