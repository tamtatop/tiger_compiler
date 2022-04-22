package com.tiger;

import com.tiger.antlr.TigerBaseVisitor;
import com.tiger.antlr.TigerLexer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import com.tiger.antlr.TigerParser;

import java.io.*;
import java.nio.file.Path;
import java.util.List;


public class Main {


    private static Writer writerOrSinkFromFilename(String filename) {
        Writer f = null;
        if (filename != null) {
            try {
                f = new FileWriter(filename);
            } catch (IOException e) {
                System.err.println("Can't create file " + filename);
                System.exit(1);
            }
        } else {
            f = new NullWriter();
        }

        return f;
    }

    public static CharStream charStreamFromFilename(String filename) {
        CharStream charStream = null;
        try {
            charStream = CharStreams.fromPath(Path.of(filename));
        } catch (IOException e) {
            System.err.println("Input file does not exist");
            System.exit(1);
        }
        return charStream;
    }

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

            SemanticVisitor semanticVisitor = new SemanticVisitor();
            semanticVisitor.visit(tree);
        } catch (IOException e) {
            System.err.println("Could not create parser output file");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        TigerArgs tigerArgs = new TigerArgs(args);


        CharStream charStream = charStreamFromFilename(tigerArgs.inputFilename);
        Writer lexerWriter = writerOrSinkFromFilename(tigerArgs.lexerFilename);
        Writer parserWriter = writerOrSinkFromFilename(tigerArgs.parserFilename);

        TigerLexer lexer = new TigerLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new FailingErrorListener("Lexer error", 2));

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        TigerParser parser = new TigerParser(tokens);
        parser.addErrorListener(new FailingErrorListener("Parser error", 3));

        generateTokens(lexer, lexerWriter);
        lexer.reset();
        generateGraph(lexer, parser, parserWriter);
    }


}
