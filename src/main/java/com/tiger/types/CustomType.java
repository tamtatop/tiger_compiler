package com.tiger.types;

public class CustomType implements Type {
    String typeId;
    TypeStructure typeStructure;

    public CustomType(String typeId, TypeStructure underlyingTypeStructure) {
        this.typeId = typeId;
        this.typeStructure = underlyingTypeStructure;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.CUSTOM;
    }

    @Override
    public TypeStructure typeStructure() {
        return typeStructure;
    }

    @Override
    public String format() {
        return typeId;
    }
}
