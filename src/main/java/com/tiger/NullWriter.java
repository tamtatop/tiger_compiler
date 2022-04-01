package com.tiger;

import java.io.IOException;
import java.io.Writer;

public class NullWriter extends Writer {
    @Override
    public void write(char[] chars, int i, int i1) throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
