package com.tiger.types;

class IntType implements Type {
    @Override
    public TypeKind getKind() {
        return TypeKind.INT;
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
