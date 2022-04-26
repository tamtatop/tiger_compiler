package com.tiger.types;

public class CustomType implements Type {
    String typeId;

    public CustomType(String typeId) {
        this.typeId = typeId;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.CUSTOM;
    }

    @Override
    public boolean sameType(Type type) {
        return type.getKind() == getKind() && ((CustomType) type).typeId.equals(typeId);
    }

    @Override
    public String format() {
        return typeId;
    }
}
