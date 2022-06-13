package com.tiger;

import com.tiger.types.TypeStructure;

public class BackendVariable {
    public String name;
    public TypeStructure typeStructure;
    public int registerIndex;
    public boolean isSpilled;

    public BackendVariable(String name, TypeStructure typeStructure) {
        this.name = name;
        this.typeStructure = typeStructure;
    }

    public void spill() {
        isSpilled = true;
    }

    public void assignRegister(int registerIndex) {
        this.registerIndex = registerIndex;
    }

}
