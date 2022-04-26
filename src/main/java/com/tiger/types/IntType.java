package com.tiger.types;

public class IntType implements Type {
    @Override
    public TypeKind getKind() {
        return TypeKind.INT;
    }

    @Override
    public boolean isSameType(Type type) {
        return type.getKind() == getKind();
    }

    @Override
    public String format() {
        return "int";
    }
}
