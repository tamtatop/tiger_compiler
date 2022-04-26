package com.tiger.types;

import com.tiger.Type;
import com.tiger.TypeKind;

class CustomType implements Type {
    String typeId;

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
        return "int";
    }
}
