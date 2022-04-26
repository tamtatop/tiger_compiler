package com.tiger.types;

public class FloatType implements Type {
    @Override
    public TypeKind getKind() {
        return TypeKind.FLOAT;
    }

    @Override
    public boolean sameType(Type type) {
        return type.getKind() == getKind();
    }

    @Override
    public String format() {
        return "int";
    }
}
