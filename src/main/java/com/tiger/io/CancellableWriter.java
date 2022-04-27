package com.tiger.io;

import java.io.IOException;
import java.io.Writer;

public class CancellableWriter implements ICancellableWriter {
    Writer writer;
    StringBuffer buffer;


    @Override
    public void write(String data) {
        buffer.append(data);
    }

    @Override
    public void cancel() {
        // nop
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("failed to close writer");
            System.exit(9); // TODO: figure out proper status code
        }
    }

    @Override
    public void commit() {
        try {
            writer.write(buffer.toString());
            writer.close();
        } catch (IOException e) {
            System.err.println("failed to write to output file");
            System.exit(9); // TODO: figure out proper status code
        }
    }
}
