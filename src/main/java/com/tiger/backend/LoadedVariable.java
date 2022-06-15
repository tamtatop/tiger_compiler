package com.tiger.backend;

import com.tiger.BackendException;
import com.tiger.BackendVariable;

public class LoadedVariable {
    String loadedRegister;
    BackendVariable backing;

    public LoadedVariable(BackendVariable backing, TemporaryRegisterAllocator tempAllocator) {
        this.backing = backing;
        assert backing.allocated;
        assert !backing.typeStructure.isArray();
        if(backing.isSpilled) {
            this.loadedRegister = tempAllocator.popTempOfType(backing.typeStructure.base);
        } else {
            this.loadedRegister = backing.getAssignedRegister();
        }
    }

    public String loadAssembly() {
        // assembly to load variable value into liftedRegister
        return "";
    }

    public String getRegister() {
        return loadedRegister;
    }

    public String flushAssembly() {
        return "";
    }
}
