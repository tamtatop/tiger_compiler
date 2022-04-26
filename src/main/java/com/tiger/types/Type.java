package com.tiger.types;


public interface Type {
    TypeKind getKind();

    boolean sameType(Type type);

    String format();
}
