package com.tiger;

import com.tiger.types.TypeStructure;

public class NakedVariable {
    public String name;
    public String scopeName;
    public TypeStructure typeStructure;

    public NakedVariable(String name, String scopeName, TypeStructure typeStructure) {
        this.name = name;
        this.scopeName = scopeName;
        this.typeStructure = typeStructure;
    }
}
