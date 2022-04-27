package com.tiger.types;

public class IntType implements Type {
    private final TypeStructure typeStructure = new TypeStructure(BaseType.INT, 0);

    @Override
    public TypeKind getKind() {
        return TypeKind.INT;
    }

    @Override
    public TypeStructure typeStructure() {
        return typeStructure;
    }

    @Override
    public String format() {
        return typeStructure.base.format();
    }
}
