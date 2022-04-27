package com.tiger.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileCancellableWriter implements CancellableWriter {

    String filename;
    StringBuilder buffer = new StringBuilder();

    public FileCancellableWriter(String filename) {
        this.filename = filename;
    }

    @Override
    public void write(String data) {
        buffer.append(data);
    }

    @Override
    public void cancel() {
        buffer = null;
    }

    @Override
    public void commit() {
        try {
            Writer writer = new FileWriter(this.filename);
            writer.write(buffer.toString());
            writer.close();
        } catch (IOException e) {
            System.err.println("Can't create file " + filename);
            System.exit(1);
        }
    }
}
