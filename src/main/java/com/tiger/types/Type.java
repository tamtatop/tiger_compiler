package com.tiger.types;


public interface Type {
    TypeKind getKind();

    default boolean isSameType(Type other) {
        return this.typeStructure().isSame(other.typeStructure());
    }

    TypeStructure typeStructure();

    String format();
}