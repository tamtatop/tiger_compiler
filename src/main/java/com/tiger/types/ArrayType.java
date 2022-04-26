package com.tiger.types;

import com.tiger.Type;
import com.tiger.TypeKind;

class ArrayType implements Type {
    int count;
    Type elementType;

    @Override
    public TypeKind getKind() {
        return TypeKind.ARRAY;
    }

    @Override
    public boolean sameType(Type type) {
        return type.getKind() == getKind() && ((ArrayType)type).count == count && ((ArrayType)type).elementType == elementType;
    }

    @Override
    public String format() {
        return "int";
    }
}
