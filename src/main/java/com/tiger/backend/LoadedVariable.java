package com.tiger.backend;

import com.tiger.BackendVariable;

public class LoadedVariable {
    String loadedRegister;
    BackendVariable backing;

    public LoadedVariable(BackendVariable backing, TemporaryRegisterAllocator tempAllocator) {
        this.backing = backing;
    }

    String loadAssembly() {
        // assembly to load variable value into liftedRegister
        return "";
    }

    String getRegister() {
        return loadedRegister;
    }

    String flushAssembly() {
        return "";
    }
}
