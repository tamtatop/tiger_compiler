package com.tiger.types;


public interface Type {
    TypeKind getKind();

    boolean isSameType(Type type);

    String format();
}
