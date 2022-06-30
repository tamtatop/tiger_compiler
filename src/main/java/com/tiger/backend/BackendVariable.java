package com.tiger.backend;

import com.tiger.NakedVariable;
import com.tiger.backend.TemporaryRegisterAllocator;
import com.tiger.types.BaseType;
import com.tiger.types.TypeStructure;

import static com.tiger.ir.IrGenerator.mangledName;


public class BackendVariable {
    public final static int WORD_SIZE = 4;

    // TODO: add more here
//    public String[] ALLOCATABLE_INT_REGISTERS = new String[]{"$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7"};
//    public String[] ALLOCATABLE_FLOAT_REGISTERS = new String[]{"$f20", "$f21", "$f22", "$f23", "$f24", "$f25"};

    public String name;
    public TypeStructure typeStructure;
    public boolean isStatic;
    public String register;
    public boolean isSpilled;
    public boolean allocated;
    public int stackOffset;
    public int spillCost;
    private boolean[] livenessUses = null;

    public BackendVariable(NakedVariable nakedBase, boolean isStatic) {
        this.name = mangledName(nakedBase);
        this.typeStructure = nakedBase.typeStructure;
        this.allocated = isStatic;
        this.isStatic = isStatic;
    }

    public BackendVariable(String name, TypeStructure typeStructure) {
        this.name = name;
        this.typeStructure = typeStructure;
        this.register = null;
    }

    public void spill() {
        assert !isStatic;
        assert !allocated;
        isSpilled = true;
        allocated = true;
    }

    public void assignRegister(String register) {
        assert !isStatic;
        assert !allocated;
        this.register = register;
        allocated = true;
    }

    public void resetAllocation() {
        if(isStatic)return;
        allocated = false;
        isSpilled = false;
        register = null;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public int sizeInBytes(){
        return typeStructure.isArray() ? typeStructure.arraySize * WORD_SIZE : WORD_SIZE;
    }

    public String getAssignedRegister() {
        assert this.allocated : "BackendVariable is not yet allocated";
        assert this.register != null : "BackendVariable is not allocated in a register";
//        if (this.typeStructure.base == BaseType.INT) {
//            return ALLOCATABLE_INT_REGISTERS[this.registerIndex];
//        } else {
//            return ALLOCATABLE_FLOAT_REGISTERS[this.registerIndex];
//        }
        return register;
    }

    public String staticName() {
        return name;
    }

    public void setLivenessBooleans(boolean[] uses) {
        this.livenessUses = uses;
    }

    public boolean isUsedAt(int idx) {
        assert this.livenessUses != null;
        return this.livenessUses[idx];
    }

    public boolean doesIntersect(BackendVariable other) {
        for (int i = 0; i < this.livenessUses.length; i++) {
            if(this.isUsedAt(i) && other.isUsedAt(i)) {
                return true;
            }
        }
        return false;
    }
}