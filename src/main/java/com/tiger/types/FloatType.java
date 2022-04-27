package com.tiger.types;

public class FloatType implements Type {
    private final TypeStructure typeStructure = new TypeStructure(BaseType.FLOAT, 0);

    @Override
    public TypeKind getKind() {
        return TypeKind.FLOAT;
    }

    @Override
    public TypeStructure typeStructure() {
        return typeStructure;
    }

    @Override
    public String format() {
        return "float";
    }
}
