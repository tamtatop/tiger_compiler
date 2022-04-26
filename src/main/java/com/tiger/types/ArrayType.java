package com.tiger.types;

public class ArrayType implements Type {
    int size;
    Type elementType;

    public ArrayType(int size, Type elementType) {
        assert size > 0;
        assert elementType.getKind() == TypeKind.FLOAT || elementType.getKind() == TypeKind.INT;
        this.size = size;
        this.elementType = elementType;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.ARRAY;
    }

    @Override
    public boolean isSameType(Type type) {
        return type.getKind() == getKind() && ((ArrayType)type).size == size && ((ArrayType)type).elementType == elementType;
    }

    @Override
    public String format() {
        return String.format("array [%d] of %s", size, elementType.format());
    }
}
