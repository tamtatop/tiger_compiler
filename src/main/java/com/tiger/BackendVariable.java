package com.tiger;

import com.tiger.backend.TemporaryRegisterAllocator;
import com.tiger.types.BaseType;
import com.tiger.types.TypeStructure;


public class BackendVariable {
    final int WORD_SIZE = 4;

    // TODO: add more here
    public String[] ALLOCATABLE_INT_REGISTERS = new String[]{"$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7"};
    public String[] ALLOCATABLE_FLOAT_REGISTERS = new String[]{"$f20", "$f21", "$f22", "$f23", "$f24", "$f25"};

    public String name;
    public TypeStructure typeStructure;
    public boolean isStatic;
    public int registerIndex;
    public boolean isSpilled;
    public boolean allocated;
    public int stackOffset;

    public BackendVariable(NakedVariable nakedBase, boolean isStatic) {
        this.name = nakedBase.name;
        this.typeStructure = nakedBase.typeStructure;
        this.allocated = false;
        this.registerIndex = -1;
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

    public int sizeInBytes(){
        return typeStructure.isArray() ? typeStructure.arraySize * WORD_SIZE : WORD_SIZE;
    }

    public String getAssignedRegister() {
        assert this.allocated : "BackendVariable is not yet allocated";
        assert this.registerIndex != -1 : "BackendVariable is not allocated in a register";
        if (this.typeStructure.base == BaseType.INT) {
            return ALLOCATABLE_INT_REGISTERS[this.registerIndex];
        } else {
            return ALLOCATABLE_FLOAT_REGISTERS[this.registerIndex];
        }
    }

    public String staticName() {
        return name;
    }
}