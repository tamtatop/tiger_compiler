package com.tiger;

import com.tiger.types.TypeStructure;

public class BackendVariable {
    public String name;
    public TypeStructure typeStructure;
    public boolean isStatic;
    public int registerIndex;
    public boolean isSpilled;
    public boolean allocated;

    public BackendVariable(NakedVariable nakedBase, boolean isStatic) {
        this.name = nakedBase.name;
        this.typeStructure = nakedBase.typeStructure;
        this.allocated = false;
        this.isStatic = isStatic;
    }

    public BackendVariable(String name, TypeStructure typeStructure) {
        this.name = name;
        this.typeStructure = typeStructure;
    }

    public void spill() {
        assert !isStatic;
        assert !allocated;
        isSpilled = true;
        allocated = true;
    }

    public void assignRegister(int registerIndex) {
        assert !isStatic;
        assert !allocated;
        this.registerIndex = registerIndex;
        allocated = true;
    }

    public boolean isStatic() {
        return isStatic;
    }

}
