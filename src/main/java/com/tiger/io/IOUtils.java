package com.tiger.io;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;
import java.nio.file.Path;

public class IOUtils {
    public static CancellableWriter writerOrSinkFromFilename(String filename) {
        CancellableWriter f;
        if (filename != null) {
            f = new FileCancellableWriter(filename);
        } else {
            f = new NullCancellableWriter();
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