package com.tiger;

import com.tiger.antlr.TigerLexer;
import com.tiger.antlr.TigerParser;
import com.tiger.io.CancellableWriter;
import com.tiger.io.IOUtils;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;


public class Main {

    public static void generateTokens(TigerLexer tigerLexer, CancellableWriter lexerWriter) {
        Vocabulary vocabulary = tigerLexer.getVocabulary();
        List<? extends Token> allTokens = tigerLexer.getAllTokens();

        for (Token token : allTokens) {
            String symbolicName = vocabulary.getSymbolicName(token.getType());
            String text = token.getText();
            lexerWriter.write("<" + symbolicName + ", " + "\"" + text + "\"" + ">\n");
        }

        lexerWriter.commit();
    }


    private static void generateGraph(TigerLexer tigerLexer, TigerParser parser, CancellableWriter parserWriter) {

        ParseTree tree = parser.tiger_program();

        parserWriter.write("digraph G {\n");
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new GraphVizGeneratorListener(parserWriter, tigerLexer.getVocabulary(), parser.getRuleNames()), tree);
        parserWriter.write("}\n");
        parserWriter.commit();

    }

    public static void main(String[] args) {
        TigerArgs tigerArgs = new TigerArgs(args);


        CharStream charStream = IOUtils.charStreamFromFilename(tigerArgs.inputFilename);
        CancellableWriter lexerWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.lexerFilename);
        CancellableWriter parserWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.parserFilename);
        CancellableWriter symbolTableWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.symbolTableFilename);
        CancellableWriter irWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.irFilename);

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
        SemanticChecker semanticChecker = new SemanticChecker(symbolTableWriter, irWriter);
        try {
            semanticChecker.visitTigerProgram(parser.tiger_program());
            symbolTableWriter.commit();
            irWriter.commit();
        } catch (SemanticException e) {
            symbolTableWriter.cancel();
            irWriter.cancel();
            System.err.printf("line %d:%d %s\n", e.line_number, e.column_number, e.message);
        }

    }


}
