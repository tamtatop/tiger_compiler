package com.tiger;

import com.tiger.antlr.TigerLexer;
import org.antlr.v4.runtime.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        if(!args[0].equals("-i") || args.length < 2) {
            System.out.println("Usage: <program> -i filename");
            return;
        }
        String filename = args[1];
        String outFilename = args[2];

        System.out.print("Parsing: " + filename);
        CharStream charStream = CharStreams.fromPath(Path.of(filename));
        TigerLexer tigerLexer = new TigerLexer(charStream);
//        CommonTokenStream commonTokenStream = new CommonTokenStream(tigerLexer);
//        System.out.print(commonTokenStream);

        Vocabulary vocabulary = tigerLexer.getVocabulary();
        List<? extends Token> allTokens = tigerLexer.getAllTokens();

        BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename));

        for (Token token : allTokens) {

            String symbolicName = vocabulary.getSymbolicName(token.getType());
            String text = token.getText();
            writer.write("<" + symbolicName + ", " + "\"" + text + "\"" +">\n");
            System.out.println("<" + symbolicName + ", " + "\"" + text + "\"" +">");
        }
        writer.close();

    }
}
