package com.tiger.types;

public class ArrayType implements Type {
    TypeStructure typeStructure;

    public ArrayType(int size, BaseType elementType) {
        assert size > 0;
        this.typeStructure = new TypeStructure(elementType, size);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.ARRAY;
    }

    @Override
    public TypeStructure typeStructure() {
        return typeStructure;
    }

    @Override
    public String format() {
        return String.format("array [%d] of %s", typeStructure.arraySize, typeStructure.base.format());
    }
}
