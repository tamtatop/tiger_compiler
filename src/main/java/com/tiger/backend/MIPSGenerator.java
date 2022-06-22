package com.tiger.backend;

import com.tiger.io.CancellableWriter;
import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.ir.interfaces.IRInstruction;
import com.tiger.ir.interfaces.IRentry;
import com.tiger.ir.interfaces.ProgramIR;
import com.tiger.types.BaseType;
import com.tiger.types.TypeStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.tiger.backend.BackendVariable.WORD_SIZE;

// for a := arr[i] or arr[i] := a
class ArrStoreLoadData {
    LoadedVariable a;
    String arrAddressRegister;

    public ArrStoreLoadData(LoadedVariable a, String arrAddressRegister) {
        this.a = a;
        this.arrAddressRegister = arrAddressRegister;
    }
}


public class MIPSGenerator {
    private final CancellableWriter writer;
    private static final HashMap<String, String> asmBinaryOp = new HashMap<>();
    private static final HashMap<String, String> asmIntBranchOp = new HashMap<>();
    private static final HashMap<String, FloatBranchOps> asmFloatBranchOp = new HashMap<>();
    private static final ArrayList<String> intSaveRegs = new ArrayList<>(List.of("$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7"));
    private static final ArrayList<String> floatSaveRegs = new ArrayList<>(List.of("$f20", "$f21", "$f22", "$f23", "$f24", "$f25", "$f26", "$f27", "$f28", "$f29", "$f30"));

    private static class FloatBranchOps {
        String compareAndSetFlagOp;
        String branchOp;

        public FloatBranchOps(String compareAndSetFlagOp, String branchOp) {
            this.compareAndSetFlagOp = compareAndSetFlagOp;
            this.branchOp = branchOp;
        }
    }

    static {
        asmBinaryOp.put("add", "add");
        asmBinaryOp.put("sub", "sub");
        asmBinaryOp.put("mult", "mul");
        asmBinaryOp.put("div", "div");
        asmBinaryOp.put("and", "and");
        asmBinaryOp.put("or", "or");

        asmIntBranchOp.put("breq", "beq");
        asmIntBranchOp.put("brneq", "bne");
        asmIntBranchOp.put("brlt", "blt");
        asmIntBranchOp.put("brgt", "bgt");
        asmIntBranchOp.put("brleq", "ble");
        asmIntBranchOp.put("brgeq", "bge");

        asmFloatBranchOp.put("breq", new FloatBranchOps("c.eq.s", "bc1t"));
        asmFloatBranchOp.put("brneq", new FloatBranchOps("c.eq.s", "bc1f"));
        asmFloatBranchOp.put("brlt", new FloatBranchOps("c.lt.s", "bc1t"));
        asmFloatBranchOp.put("brgt", new FloatBranchOps("c.le.s", "bc1f"));
        asmFloatBranchOp.put("brleq", new FloatBranchOps("c.le.s", "bc1t"));
        asmFloatBranchOp.put("brgeq", new FloatBranchOps("c.lt.s", "bc1f"));

    }

    public MIPSGenerator(CancellableWriter writer) {
        this.writer = writer;
    }

    public void translateProgram(ProgramIR programIR) {
        translateStaticDataSection(programIR);

        writer.write(".text\n");
        for (FunctionIR functionIR : programIR.getFunctions()) {
            translateFunction(functionIR, programIR);
        }
        writer.write("""

                _fun_printi:
                li $v0, 1
                syscall
                li $v0, 11
                li $a0, 10
                syscall
                jr $ra

                _fun_printf:
                li $v0, 2
                syscall
                li $v0, 11
                li $a0, 10
                syscall
                jr $ra


                _fun_not:
                beq $a0, $zero, not0
                not1:
                move $v0, $zero
                jr $ra
                not0:
                li $v0, 1
                jr $ra


                _fun_exit:
                li $v0, 17
                syscall
                jr $ra
                """);
    }

    private void translateStaticDataSection(ProgramIR programIR) {
        writer.write(".data\n");
        for (BackendVariable staticVar : programIR.getStaticVariables()) {
            String description = "";
            if (staticVar.typeStructure.isArray()) {
                description = String.format(".space %s", staticVar.sizeInBytes());
            } else if (staticVar.typeStructure.base == BaseType.INT) {
                description = ".word 0";
            } else if (staticVar.typeStructure.base == BaseType.FLOAT) {
                description = ".float 0.0";
            }
            writer.write(String.format("%s: %s\n", staticVar.staticName(), description));
        }
    }

    private List<BackendVariable> getFunctionSignature(String name, ProgramIR programIR) {
        if(Objects.equals(name, "printi")) {
            return new ArrayList<>(List.of(new BackendVariable("i", new TypeStructure(BaseType.INT, 0))));
        }
        if(Objects.equals(name, "printf")) {
            return new ArrayList<>(List.of(new BackendVariable("f", new TypeStructure(BaseType.FLOAT, 0))));
        }
        if(Objects.equals(name, "not")) {
            return new ArrayList<>(List.of(new BackendVariable("i", new TypeStructure(BaseType.INT, 0))));
        }
        if(Objects.equals(name, "exit")) {
            return new ArrayList<>(List.of(new BackendVariable("i", new TypeStructure(BaseType.INT, 0))));
        }
        return programIR.getFunctionByName(name).getArguments();
    }

    private BaseType getReturnType(String name, ProgramIR programIR) {
        if(Objects.equals(name, "printi")) {
            return null;
        }
        if(Objects.equals(name, "printf")) {
            return null;
        }
        if(Objects.equals(name, "not")) {
            return BaseType.INT;
        }
        if(Objects.equals(name, "exit")) {
            return null;
        }
        return programIR.getFunctionByName(name).getReturnType();
    }


    public void translateFunction(FunctionIR functionIR, ProgramIR programIR) {
        if (functionIR.getFunctionName().equals("main")) {
            writer.write(".globl main\n");
            writer.write("main" + ":\n");
        }
        writer.write("_fun_" + functionIR.getFunctionName() + ":\n");

        // space to store old fp
        writer.write(String.format("addiu $sp, $sp, -%d\n", WORD_SIZE));
        writer.write("sw $fp, 0($sp)\n");
        writer.write("move $fp, $sp\n");

        // ======================== STACK LAYOUT ====================================================
        //         sp                                     fp
        //         V                                       V
        //         |          stack frame                  | old fp | stackarg0 | stackarg1 ...
        //  sizes: |           spOffset                    |   4    |     4     |   4       ...
        //         | saved ra | saved regs | spilled vars  |
        // ==========================================================================================

        // saved spilled vars
        int spOffset = 0;
        for (BackendVariable localVariable : functionIR.getLocalVariables()) {
            if (localVariable.isSpilled) {
                spOffset += localVariable.sizeInBytes();
                localVariable.stackOffset = -spOffset;
            }
        }

        // saved regs
        spOffset += intSaveRegs.size() * WORD_SIZE + floatSaveRegs.size() * WORD_SIZE;
        int saveRegOffset = -spOffset;

        // saved ra
        spOffset += WORD_SIZE;

        // allocate stack frame
        writer.write(String.format("addiu $sp, $sp, -%d\n", spOffset));

        // actually save regs and ra
        //handleSaveRegData(saveRegOffset, "sw", "s.s");
        writer.write(String.format("sw $ra, %d($fp)\n", -spOffset));

        // ARGUMENT HANDLING:
        {
            ArgumentRegisterAllocator argumentRegisterAllocator = new ArgumentRegisterAllocator();
            int stackArgCounter = 0;
            for (BackendVariable argument : functionIR.getArguments()) {
                // we can reset temp allocation on each copy
                TemporaryRegisterAllocator temporaryRegisterAllocator = new TemporaryRegisterAllocator();

                String sourceReg = argumentRegisterAllocator.popArgOfType(argument.typeStructure.base);
                LoadedVariable target = new LoadedVariable(argument.name, functionIR, temporaryRegisterAllocator, argument.typeStructure.base);


                if (sourceReg == null) {
                    // arg is in stack
                    String loadInstr = switch (argument.typeStructure.base) {
                        case INT -> "lw";
                        case FLOAT -> "l.s";
                    };
                    writer.write(String.format("%s %s, %d($fp)\n", loadInstr, target.getRegister(), WORD_SIZE + WORD_SIZE * stackArgCounter));
                    stackArgCounter += 1;
                } else {
                    // arg is in sourceReg
                    String moveInstr = switch (argument.typeStructure.base) {
                        case INT -> "move";
                        case FLOAT -> "mov.s";
                    };
                    writer.write(String.format("%s %s, %s\n", moveInstr, target.getRegister(), sourceReg));
                }
                writer.write(target.flushAssembly());
            }
        }

        for (IRentry iRentry : functionIR.getBody()) {
            if (iRentry.isInstruction()) {
                IRInstruction instr = iRentry.asInstruction();
                switch (instr.getType()) {
                    case ASSIGN -> {
                        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();
                        String aName = instr.getIthCode(1);
                        String bName = instr.getIthCode(2);
                        String cName = instr.getIthCode(3);

                        BackendVariable a = functionIR.fetchVariableByName(aName);
                        if (cName == null && !a.typeStructure.isArray()) {
                            // assign, a, b,
                            LoadedVariable aLoaded = new LoadedVariable(aName, functionIR, tempRegisterAllocator, a.typeStructure.base);
                            String aRegister = aLoaded.getRegister();

                            LoadedVariable bLoaded = new LoadedVariable(bName, functionIR, tempRegisterAllocator, a.typeStructure.base);
                            String bRegister = bLoaded.getRegister();
                            writer.write(bLoaded.loadAssembly());
                            String moveInstruction = switch (a.typeStructure.base) {
                                case INT -> "move";
                                case FLOAT -> "mov.s";
                            };
                            writer.write(String.format("%s %s, %s\n", moveInstruction, aRegister, bRegister));
                            writer.write(aLoaded.flushAssembly());
                        } else if (cName != null) {
                            // assign, X, 100, 10
                            // X = [10]*100
                            String XRegister = loadArrayBeginning(tempRegisterAllocator, a);
                            LoadedVariable v = new LoadedVariable(cName, functionIR, tempRegisterAllocator, a.typeStructure.base);
                            writer.write(v.loadAssembly());

                            String storeInstruction = switch (a.typeStructure.base) {
                                case INT -> "sw";
                                case FLOAT -> "s.s";
                            };

                            for (int i = 0; i < Integer.parseInt(bName); i++) {
                                writer.write(String.format("%s %s, %d(%s)\n", storeInstruction, v.getRegister(), WORD_SIZE * i, XRegister));
                            }
                        } else if (a.typeStructure.isArray()) {
                            // assign, X, Y,
                            // X[..] = Y[..]
                            String XRegister = loadArrayBeginning(tempRegisterAllocator, a);
                            BackendVariable b = functionIR.fetchVariableByName(bName);
                            String YRegister = loadArrayBeginning(tempRegisterAllocator, b);

                            String storeInstruction = switch (a.typeStructure.base) {
                                case INT -> "sw";
                                case FLOAT -> "s.s";
                            };

                            String loadInstruction = switch (a.typeStructure.base) {
                                case INT -> "lw";
                                case FLOAT -> "l.s";
                            };

                            String copyRegister = tempRegisterAllocator.popInt();

                            for (int i = 0; i < a.typeStructure.arraySize; i++) {
                                writer.write(String.format("%s %s, %d(%s)\n", loadInstruction, copyRegister, WORD_SIZE * i, YRegister));
                                writer.write(String.format("%s %s, %d(%s)\n", storeInstruction, copyRegister, WORD_SIZE * i, XRegister));
                            }
                        }
                    }
                    case BINOP -> translateBinaryOperation(asmBinaryOp.get(instr.getIthCode(0)), instr, functionIR);
                    case GOTO -> {
                        String afterLoop = instr.getIthCode(1);
                        writer.write(String.format("j %s\n", afterLoop));
                    }
                    case BRANCH -> {
                        translateBranchOperation(instr.getIthCode(0), instr, functionIR);
                    }
                    case RETURN -> {
                        String returnVarName = instr.getIthCode(1);
                        if (returnVarName != null) {
                            LoadedVariable retVar = new LoadedVariable(returnVarName, functionIR, new TemporaryRegisterAllocator(), functionIR.getReturnType());
                            writer.write(retVar.loadAssembly());
                            String retVarRegister = retVar.getRegister();
                            if (functionIR.getReturnType() == BaseType.INT) {
                                writer.write(String.format("move, $v0, %s\n", retVarRegister));
                            } else {
                                writer.write(String.format("mov.s, $f0, %s\n", retVarRegister));
                            }
                        }
                        //handleSaveRegData(saveRegOffset, "lw", "l.s");
                        writer.write(String.format("lw $ra, %d($fp)\n", -spOffset));
                        writer.write(String.format("addiu $sp, $sp, %d\n", spOffset));

                        // restore old fp
                        writer.write("lw $fp, 0($sp)\n");
                        // remove space allocated for storing old fp
                        writer.write(String.format("addiu $sp, $sp, %d\n", WORD_SIZE));

                        writer.write("jr $ra\n");

                    }
                    case CALL -> {
                        ArgumentRegisterAllocator argRegisterAllocator = new ArgumentRegisterAllocator();
                        int i = 1;
                        String flushVarName = "";
                        if (instr.getIthCode(0).equals("callr")) {
                            flushVarName = instr.getIthCode(i);
                            i = 2;
                        }
                        String callingFunctionName = instr.getIthCode(i);
                       // FunctionIR callingFunction = programIR.getFunctionByName(callingFunctionName);
                        List<BackendVariable> arguments = getFunctionSignature(callingFunctionName, programIR);
                        i += 1;
                        int stackVarIdx = 0;
                        assert instr.size() == i + arguments.size();

                        for (BackendVariable argument : arguments) {
                            BaseType argType = argument.typeStructure.base;
                            String argRegister = argRegisterAllocator.popArgOfType(argType);
                            if (argRegister == null) {
                                stackVarIdx += 1;
                            }
                        }
                        writer.write(String.format("addiu $sp, $sp, %d\n", -WORD_SIZE*stackVarIdx));

                        argRegisterAllocator = new ArgumentRegisterAllocator();
                        stackVarIdx=0;

                        for (int argIdx = 0; argIdx < arguments.size(); argIdx++) {
                            // we can reset temp allocation on each copy
                            TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();

                            String argName = instr.getIthCode(i + argIdx);
                            BaseType argType = arguments.get(argIdx).typeStructure.base;
                            LoadedVariable arg = new LoadedVariable(argName, functionIR, tempRegisterAllocator, argType);
                            writer.write(arg.loadAssembly());
                            String argRegister = argRegisterAllocator.popArgOfType(argType);

                            String asmInstr = "";
                            String storeInstr = "";
                            switch (argType) {
                                case INT -> {
                                    asmInstr = "move";
                                    storeInstr = "sw";
                                }
                                case FLOAT -> {
                                    asmInstr = "mov.s";
                                    storeInstr = "s.s";
                                }
                            }
                            if (argRegister == null) {
                                stackVarIdx += 1;
                                writer.write(String.format("%s %s, %d($fp)\n", storeInstr, arg.getRegister(), -spOffset - stackVarIdx * 4));
                            } else {
                                writer.write(String.format("%s %s, %s\n", asmInstr, argRegister, arg.getRegister()));
                            }
                        }
                        // CHEATING ;)
                        writer.write(String.format("jal %s\n", "_fun_" + callingFunctionName));
                        writer.write(String.format("addiu $sp, $sp, %d\n", WORD_SIZE*stackVarIdx));

                        if (instr.getIthCode(0).equals("callr")) {
                            TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();

                            BaseType flushVarType = functionIR.fetchVariableByName(flushVarName).typeStructure.base;
                            LoadedVariable flushVar = new LoadedVariable(flushVarName, functionIR, tempRegisterAllocator, flushVarType);
                            String returnedValueRegister = "";
                            String moveInstruction;
                            if (flushVarType == BaseType.INT) {
                                returnedValueRegister = "$v0";
                                moveInstruction = "move";
                            } else {
                                returnedValueRegister = "$f0";
                                moveInstruction = "mov.s";
                            }
                            if (getReturnType(callingFunctionName, programIR) == BaseType.INT && flushVarType == BaseType.FLOAT) {
                                writer.write("mtc1 $v0, $f0\n");
                                writer.write("cvt.s.w $f0, $f0\n");
                            }

                            writer.write(String.format("%s %s, %s\n", moveInstruction, flushVar.getRegister(), returnedValueRegister));
                            writer.write(flushVar.flushAssembly());
                        }

                    }
                    case ARRAYSTORE -> {
                        ArrStoreLoadData arrData = getDataForArrayStoreLoadTranslation(instr, functionIR);
                        writer.write(arrData.a.loadAssembly());
                        String storeInstruction = switch (arrData.a.type) {
                            case INT -> "sw";
                            case FLOAT -> "s.s";
                        };
                        writer.write(String.format("%s %s, 0(%s)\n", storeInstruction, arrData.a.getRegister(), arrData.arrAddressRegister));
                    }
                    case ARRAYLOAD -> {
                        ArrStoreLoadData arrData = getDataForArrayStoreLoadTranslation(instr, functionIR);

                        String loadInstruction = switch (arrData.a.type) {
                            case INT -> "lw";
                            case FLOAT -> "l.s";
                        };
                        writer.write(String.format("%s %s, 0(%s)\n", loadInstruction, arrData.a.getRegister(), arrData.arrAddressRegister));
                        writer.write(arrData.a.flushAssembly());
                    }
                }

            } else if (iRentry.isLabel()) {
                writer.write(String.format("%s:\n", iRentry.asLabel().getName()));
            }
        }
    }

    private void translateBinaryOperation(String binop, IRInstruction instr, FunctionIR functionIR) {

        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();
        String aName = instr.getIthCode(1);
        String bName = instr.getIthCode(2);
        String cName = instr.getIthCode(3);
        BackendVariable c = functionIR.fetchVariableByName(cName);

        LoadedVariable aLoaded = new LoadedVariable(aName, functionIR, tempRegisterAllocator, c.typeStructure.base);
        String aRegister = aLoaded.getRegister();
        writer.write(aLoaded.loadAssembly());

        LoadedVariable bLoaded = new LoadedVariable(bName, functionIR, tempRegisterAllocator, c.typeStructure.base);
        String bRegister = bLoaded.getRegister();
        writer.write(bLoaded.loadAssembly());

        LoadedVariable cLoaded = new LoadedVariable(cName, functionIR, tempRegisterAllocator, c.typeStructure.base);
        String cRegister = cLoaded.getRegister();

        if (!c.typeStructure.isBaseInt()) {
            binop += ".s";
        }

        writer.write(String.format("%s %s, %s, %s\n", binop, cRegister, aRegister, bRegister));
        writer.write(cLoaded.flushAssembly());
    }

    private void handleSaveRegData(int saveRegOffset, String intCommand, String floatCommand) {
        for (String saveReg : intSaveRegs) {
            saveRegOffset += WORD_SIZE;
            writer.write(String.format("%s %s, %d($fp)\n", intCommand, saveReg, saveRegOffset));
        }
        for (String saveReg : floatSaveRegs) {
            saveRegOffset += WORD_SIZE;
            writer.write(String.format("%s %s, %d($fp)\n", floatCommand, saveReg, saveRegOffset));
        }
    }

    private String loadArrayBeginning(TemporaryRegisterAllocator tempRegisterAllocator, BackendVariable a) {
        String startReg = tempRegisterAllocator.popInt();
        if (a.isStatic) {
            writer.write(String.format("la %s, %s\n", startReg, a.staticName()));
        } else {
            writer.write(String.format("move %s, $fp\n", startReg));
            writer.write(String.format("addi %s, %s, %d\n", startReg, startReg, a.stackOffset));
        }
        return startReg;
    }

    private void translateBranchOperation(String irBranchOp, IRInstruction instr, FunctionIR functionIR) {
        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();
        String aName = instr.getIthCode(1);
        String bName = instr.getIthCode(2);
        String label = instr.getIthCode(3);
        BaseType type = null;
        if(functionIR.fetchVariableByName(aName) != null){
            type = functionIR.fetchVariableByName(aName).typeStructure.base;
        }
        if(functionIR.fetchVariableByName(bName) != null){
            type = functionIR.fetchVariableByName(bName).typeStructure.base;
        }
        if(type == null){
            try {
                Integer.parseInt(aName);
                type = BaseType.INT;
            } catch (NumberFormatException e) {
                type = BaseType.FLOAT;
            }
        }

        LoadedVariable a = new LoadedVariable(aName, functionIR, tempRegisterAllocator, type);
        LoadedVariable b = new LoadedVariable(bName, functionIR, tempRegisterAllocator, type);
        writer.write(a.loadAssembly());
        writer.write(b.loadAssembly());
        if (type == BaseType.INT) {
            writer.write(String.format("%s %s, %s, %s\n", asmIntBranchOp.get(irBranchOp), a.getRegister(), b.getRegister(), label));
        } else {
            FloatBranchOps floatBranchOps = asmFloatBranchOp.get(irBranchOp);
            writer.write(String.format("%s %s, %s\n", floatBranchOps.compareAndSetFlagOp, a.getRegister(), b.getRegister()));
            writer.write(String.format("%s %s\n", floatBranchOps.branchOp, label));
        }
    }

    private ArrStoreLoadData getDataForArrayStoreLoadTranslation(IRInstruction instr, FunctionIR functionIR) {
        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();
        String arrName;
        String iName;
        String aName;


        if(Objects.equals(instr.getIthCode(0), "array_store")) {
            arrName = instr.getIthCode(1);
            iName = instr.getIthCode(2);
            aName = instr.getIthCode(3);
        } else {
            arrName = instr.getIthCode(2);
            iName = instr.getIthCode(3);
            aName = instr.getIthCode(1);
        }

        BackendVariable arr = functionIR.fetchVariableByName(arrName);
        LoadedVariable i = new LoadedVariable(iName, functionIR, tempRegisterAllocator, BaseType.INT);
        writer.write(i.loadAssembly());

        // TODO: possibly optimize to work with one less register

        writer.write(String.format("sll %s, %s, 2\n", i.getRegister(), i.getRegister()));
        String arrayBeginningRegister = loadArrayBeginning(tempRegisterAllocator, arr);
        writer.write(String.format("add %s, %s, %s\n", i.getRegister(), i.getRegister(), arrayBeginningRegister));

        LoadedVariable a = new LoadedVariable(aName, functionIR, tempRegisterAllocator, arr.typeStructure.base);

        return new ArrStoreLoadData(a, i.getRegister());
    }
}
