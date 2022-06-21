package com.tiger.backend;

import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.types.BaseType;


enum BackingType {
    CONST,
    REG,
    STACK,
    STATIC,
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
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void initWithBacking(BackendVariable backing, TemporaryRegisterAllocator tempAllocator, BaseType type) {
        this.backing = backing;
        assert backing.allocated;
        assert !backing.typeStructure.isArray();
        if (backing.isStatic) {
            this.loadedRegister = tempAllocator.popTempOfType(type);
            this.backingType = BackingType.STATIC;
        } else if (backing.isSpilled) {
            this.loadedRegister = tempAllocator.popTempOfType(type);
            this.backingType = BackingType.STACK;
        } else {
            // TODO: don't use extra register when possible
            this.loadedRegister = tempAllocator.popTempOfType(type);
            this.backingType = BackingType.REG;
        }
    }

    // TODO: consider should user of loaded variable expect that variable does not change unless flush is called?!
    public LoadedVariable(String s, FunctionIR f, TemporaryRegisterAllocator tempAllocator, BaseType type) {
        this.type = type;
        if (isNumeric(s)) {
            this.constval = s;
            this.backingType = BackingType.CONST;
            this.loadedRegister = tempAllocator.popTempOfType(type);
        } else {
            initWithBacking(f.fetchVariableByName(s), tempAllocator, type);
        }
    }

    public LoadedVariable(BackendVariable backing, TemporaryRegisterAllocator tempAllocator, BaseType type) {
        this.type = type;
        initWithBacking(backing, tempAllocator, type);
    }

    public String loadAssembly() {
        return switch (this.type) {
            case INT -> switch (this.backingType) {
                case CONST -> String.format("li %s, %s\n", this.loadedRegister, this.constval);
                case REG -> String.format("move %s, %s\n", this.loadedRegister, this.backing.getAssignedRegister());
                case STACK -> String.format("lw %s, %d($fp)\n", this.loadedRegister, this.backing.stackOffset);
                case STATIC -> String.format("lw %s, %s\n", this.loadedRegister, this.backing.staticName());
            };
            case FLOAT -> switch (this.backingType) {
                case CONST -> String.format("li.s %s, %s\n", this.loadedRegister, this.constval);
                case REG -> switch (backing.typeStructure.base) {
                    case FLOAT ->
                            String.format("move %s, %s\n", this.loadedRegister, this.backing.getAssignedRegister());
                    case INT ->
                            String.format("mtc1 %s, %s\ncvt.s.w %s, %s\n", this.loadedRegister, this.backing.getAssignedRegister(), this.loadedRegister, this.loadedRegister);
                }
                ;
                case STACK -> switch (backing.typeStructure.base) {
                    case FLOAT -> String.format("l.s %s, %d($fp)\n", this.loadedRegister, this.backing.stackOffset);
                    case INT ->
                            String.format("l.s %s, %d($fp)\ncvt.s.w %s, %s\n", this.loadedRegister, this.backing.stackOffset, this.loadedRegister, this.loadedRegister);
                }
                ;

                case STATIC -> switch (backing.typeStructure.base) {
                    case FLOAT -> String.format("l.s %s, %s\n", this.loadedRegister, this.backing.staticName());
                    case INT ->
                            String.format("l.s %s, %s\ncvt.s.w %s, %s\n", this.loadedRegister, this.backing.staticName(), this.loadedRegister, this.loadedRegister);
                }
                ;
            };
        };
    }

    public String getRegister() {
        return loadedRegister;
    }

    public String flushAssembly() {
        return switch (this.type) {
            case INT -> switch (this.backingType) {
                case CONST -> throw new RuntimeException("can't flush to const");
                case REG -> String.format("move %s, %s\n", this.backing.getAssignedRegister(), this.loadedRegister);
                case STACK -> String.format("sw %s, %d($fp)\n", this.loadedRegister, this.backing.stackOffset);
                case STATIC -> String.format("sw %s, %s\n", this.loadedRegister, this.backing.staticName());
            };
            case FLOAT -> switch (this.backingType) {
                case CONST -> throw new RuntimeException("can't flush to const");
                case REG -> {
                    assert backing.typeStructure.base == BaseType.FLOAT;
                    yield String.format("move %s, %s\n", this.backing.getAssignedRegister(), this.loadedRegister);
                }
                case STACK -> {
                    assert backing.typeStructure.base == BaseType.FLOAT;
                    yield String.format("s.s %s, %d($fp)\n", this.loadedRegister, this.backing.stackOffset);
                }
                case STATIC -> {
                    assert backing.typeStructure.base == BaseType.FLOAT;
                    yield String.format("s.s %s, %s\n", this.loadedRegister, this.backing.staticName());
                }
            };
        };
    }
}
