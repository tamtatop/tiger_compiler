package com.tiger.io;

public interface CancellableWriter {
    void write(String data);

    void cancel();

    void commit();
}
