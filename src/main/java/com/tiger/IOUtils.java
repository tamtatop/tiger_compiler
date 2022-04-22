package com.tiger;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public class IOUtils {
    static Writer writerOrSinkFromFilename(String filename) {
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
}