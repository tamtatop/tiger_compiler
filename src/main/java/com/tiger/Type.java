package com.tiger;


public interface Type {
    TypeKind getKind();

    boolean sameType(Type type);

    String format();
}
