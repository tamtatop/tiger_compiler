package com.tiger.backend;

import com.tiger.BackendException;
import com.tiger.BackendVariable;

// TODO: maybe extend this abstraction to immediate values as well
public class LoadedVariable {
    String loadedRegister;
    BackendVariable backing;

    public LoadedVariable(BackendVariable backing, TemporaryRegisterAllocator tempAllocator) {
        this.backing = backing;
        assert backing.allocated;
        assert !backing.typeStructure.isArray();
        if (backing.isSpilled) {
            this.loadedRegister = tempAllocator.popTempOfType(backing.typeStructure.base);
        } else {
            this.loadedRegister = backing.getAssignedRegister();
        }
    }

    // TODO: change to fp
    public String loadAssembly() {
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
