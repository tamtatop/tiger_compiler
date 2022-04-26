package com.tiger.types;

import com.tiger.Type;
import com.tiger.TypeKind;

class FloatType implements Type {
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
