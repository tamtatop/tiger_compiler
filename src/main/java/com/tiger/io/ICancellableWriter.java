package com.tiger.io;

public interface ICancellableWriter {
    void write(String data);

    void cancel();

    void commit();
}
