package com.tiger.backend;

import com.tiger.BackendException;
import com.tiger.BackendVariable;
import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.types.BaseType;



enum BackingType{
    CONST,
    REG,
    STACK,
}

public class LoadedVariable {
    String loadedRegister;
    BackendVariable backing;
    String constval;
    BaseType type;
    private BackingType backingType;

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public LoadedVariable(String s, FunctionIR f, TemporaryRegisterAllocator tempAllocator, BaseType type) {
        this.type = type;
        if(isNumeric(s)){
            this.constval = s;
            this.backingType = BackingType.CONST;
            this.loadedRegister = tempAllocator.popTempOfType(type);
        } else {
            this.backing = f.fetchVariableByName(s);
            assert backing.allocated;
            assert !backing.typeStructure.isArray();
            if (backing.isSpilled) {
                this.loadedRegister = tempAllocator.popTempOfType(type);
                this.backingType = BackingType.STACK;
            } else {
                // TODO: don't use extra register when possible
                this.loadedRegister = tempAllocator.popTempOfType(type);
                this.backingType = BackingType.REG;
            }
        }
    }

    // TODO: change to fp
    public String loadAssembly() {
        return switch (this.type) {
            case INT -> switch (this.backingType) {
                case CONST -> String.format("li %s, %s\n", this.loadedRegister, this.constval);
                case REG -> String.format("move %s, %s\n", this.loadedRegister, this.backing.getAssignedRegister());
                case STACK -> String.format("lw %s, %d($fp)\n", this.loadedRegister, this.backing.stackOffset);
            };
            case FLOAT -> switch (this.backingType) {
                case CONST -> String.format("li.s %s, %s\n", this.loadedRegister, this.constval);
                case REG -> "";
                case STACK -> String.format("lw %s, %d($fp)\n", this.loadedRegister, this.backing.stackOffset);
            };
        };

        if (!this.backing.isSpilled) return "";

        return switch (backing.typeStructure.base) {
            case INT -> String.format("lw %s, %d($sp)\n", loadedRegister, backing.stackOffset);
            case FLOAT -> String.format("l.s %s, %d($sp)\n", loadedRegister, backing.stackOffset);
        };
    }

    public String getRegister() {
        return loadedRegister;
    }

    public String flushAssembly() {
        if (!this.backing.isSpilled) return "";

        return switch (backing.typeStructure.base) {
            case INT -> String.format("sw %s, %d($sp)\n", loadedRegister, backing.stackOffset);
            case FLOAT -> String.format("s.s %s, %d($sp)\n", loadedRegister, backing.stackOffset);
        };
    }
}
