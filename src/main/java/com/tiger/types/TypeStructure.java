package com.tiger.types;

public class TypeStructure  {
    public BaseType base;
    public int arraySize; // zero if not array

    public TypeStructure(BaseType base, int arraySize) {
        this.base = base;
        this.arraySize = arraySize;
    }

    public boolean isArray(){
        return arraySize != 0;
    }

    public boolean isBaseInt(){
        return base == BaseType.INT && !isArray();
    }

    public boolean isSame(TypeStructure other) {
        return this.base == other.base && this.arraySize == other.arraySize;
    }
}
