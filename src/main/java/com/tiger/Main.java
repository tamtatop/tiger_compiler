package com.tiger;

import com.tiger.antlr.TigerLexer;
import jdk.jshell.spi.ExecutionControl;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import com.tiger.antlr.TigerParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


public class Main {

    public static CharStream charStreamFromFilename(String filename){
        CharStream charStream = null;
        try {
            charStream = CharStreams.fromPath(Path.of(filename));
        } catch (IOException e) {
            System.err.println("Input file does not exist");
            System.exit(1);
        }
        return charStream;
    }

    public static void writeLexerOutput(TigerLexer tigerLexer, String lexerFilename) {
        Vocabulary vocabulary = tigerLexer.getVocabulary();
        List<? extends Token> allTokens = tigerLexer.getAllTokens();
        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new FileWriter(lexerFilename));

            for (Token token : allTokens) {
                System.out.println(token);
                String symbolicName = vocabulary.getSymbolicName(token.getType());
                String text = token.getText();
                writer.write("<" + symbolicName + ", " + "\"" + text + "\"" +">\n");
                System.out.println("<" + symbolicName + ", " + "\"" + text + "\"" +">");
            }

            writer.close();
        } catch (IOException e){
            System.err.println("Could not create lexer output file");
            System.exit(1);
        }
    }


    private static void writeParserOutput(TigerLexer tigerLexer, String parserFilename) throws ExecutionControl.NotImplementedException {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(parserFilename));

            CommonTokenStream tokens = new CommonTokenStream(tigerLexer);
            TigerParser parser = new TigerParser(tokens);
            ParseTree tree = parser.tiger_program();

            writer.write("digraph G {\n");
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(new GraphVizGeneratorListener(writer, tigerLexer.getVocabulary(), parser.getRuleNames()), tree);

            writer.write("}\n");
            writer.close();
        } catch (IOException e){
            System.err.println("Could not create parser output file");
            System.exit(1);
        }
    }

    public static void main(String[] args) throws ExecutionControl.NotImplementedException {
        TigerArgs tigerArgs = new TigerArgs(args);

        if(tigerArgs.inputFilename == null) {
            System.err.println("Input filename not provided");
            System.exit(1);
        }

        CharStream charStream = charStreamFromFilename(tigerArgs.inputFilename);

        TigerLexer tigerLexer = new TigerLexer(charStream);

        tigerLexer.removeErrorListeners();
        tigerLexer.addErrorListener(LexerErrorListener.INSTANCE);

        if(tigerArgs.lexerFilename != null) {
            writeLexerOutput(tigerLexer, tigerArgs.lexerFilename);
        }
        if(tigerArgs.parserFilename != null) {
            writeParserOutput(tigerLexer, tigerArgs.parserFilename);
        }
    }

}
