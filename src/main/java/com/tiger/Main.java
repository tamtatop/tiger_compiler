package com.tiger;

import com.tiger.antlr.TigerLexer;
import com.tiger.antlr.TigerParser;
import com.tiger.backend.ArgumentRegisterAllocator;
import com.tiger.backend.LoadedVariable;
import com.tiger.backend.TemporaryRegisterAllocator;
import com.tiger.io.CancellableWriter;
import com.tiger.io.IOUtils;
import com.tiger.ir.ProgramIRBuilder;
import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.ir.interfaces.IRInstruction;
import com.tiger.ir.interfaces.IRentry;
import com.tiger.ir.interfaces.ProgramIR;
import com.tiger.types.BaseType;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tiger.BackendVariable.WORD_SIZE;


public class Main {

    public static void generateTokens(TigerLexer tigerLexer, CancellableWriter lexerWriter) {
        Vocabulary vocabulary = tigerLexer.getVocabulary();
        List<? extends Token> allTokens = tigerLexer.getAllTokens();

        for (Token token : allTokens) {
            String symbolicName = vocabulary.getSymbolicName(token.getType());
            String text = token.getText();
            lexerWriter.write("<" + symbolicName + ", " + "\"" + text + "\"" + ">\n");
        }

        lexerWriter.commit();
    }


    private static void generateGraph(TigerLexer tigerLexer, TigerParser parser, CancellableWriter parserWriter) {

        ParseTree tree = parser.tiger_program();

        parserWriter.write("digraph G {\n");
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new GraphVizGeneratorListener(parserWriter, tigerLexer.getVocabulary(), parser.getRuleNames()), tree);
        parserWriter.write("}\n");
        parserWriter.commit();

    }

    public static void main(String[] args) {
        TigerArgs tigerArgs = new TigerArgs(args);


        CharStream charStream = IOUtils.charStreamFromFilename(tigerArgs.inputFilename);
        CancellableWriter lexerWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.lexerFilename);
        CancellableWriter parserWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.parserFilename);
        CancellableWriter symbolTableWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.symbolTableFilename);
        CancellableWriter irWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.irFilename);
        CancellableWriter cfgWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.cfgFilename);
        CancellableWriter livenessWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.livenessFilename);
        CancellableWriter mipsWriter = IOUtils.writerOrSinkFromFilename(tigerArgs.mipsFilename);

        TigerLexer lexer = new TigerLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new FailingErrorListener("Lexer error", 2));

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        TigerParser parser = new TigerParser(tokens);
        parser.addErrorListener(new FailingErrorListener("Parser error", 3));

        generateTokens(lexer, lexerWriter);
        lexer.reset();
        generateGraph(lexer, parser, parserWriter);

        SemanticErrorLogger errorLogger = new SemanticErrorLogger();

        // Experiments
        lexer.reset();
        parser.reset();
        ProgramIRBuilder listener = new ProgramIRBuilder();
        CompilerFront compilerFront = new CompilerFront(symbolTableWriter, irWriter, errorLogger, listener);

        compilerFront.visitTigerProgram(parser.tiger_program());

        if (errorLogger.anyError()) {
            symbolTableWriter.cancel();
            irWriter.cancel();
            System.exit(4);
        }
        symbolTableWriter.commit();
        irWriter.commit();

        ProgramIR finalIr = listener.getProgramIR();
        System.out.println("program name: " + finalIr.getProgramName());

        CompilerBackend.runBackend(finalIr, cfgWriter, livenessWriter, mipsWriter);
    }


}


class CompilerBackend {

    public static void runBackend(ProgramIR ir, CancellableWriter cfgWriter, CancellableWriter livenessWriter, CancellableWriter mipsWriter) {

    }

}

interface RegisterAllocator {

    void runAllocationAlgorithm(ProgramIR ir);

}

class NaiveRegisterAllocator implements RegisterAllocator {

    @Override
    public void runAllocationAlgorithm(ProgramIR ir) {
        for (FunctionIR functionIR : ir.getFunctions()) {
            for (BackendVariable localVariable : functionIR.getLocalVariables()) {
                localVariable.spill();
            }
        }
    }
}

class MIPSGenerator {
    private final CancellableWriter writer;
    private static final HashMap<String, String> asmBinaryOp = new HashMap<>();
    private static final HashMap<String, String> asmIntBranchOp = new HashMap<>();
    private static final HashMap<String, String> asmFloatBranchOp = new HashMap<>();
    private static final ArrayList<String> intSaveRegs = new ArrayList<>(List.of("$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7"));
    private static final ArrayList<String> floatSaveRegs = new ArrayList<>(List.of("$f20", "$f21", "$f22", "$f23", "$f24", "$f25", "$f26", "$f27", "$f28", "$f29", "$f30"));

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

        asmFloatBranchOp.put("breq", "c.eq.s");
        asmFloatBranchOp.put("brneq", "c.ne.s");
        asmFloatBranchOp.put("brlt", "c.lt.s");
        asmFloatBranchOp.put("brgt", "c.gt.s");
        asmFloatBranchOp.put("brleq", "c.le.s");
        asmFloatBranchOp.put("brgeq", "c.ge.s");

    }

    public MIPSGenerator(CancellableWriter writer) {
        this.writer = writer;
    }

    public void translateProgram(ProgramIR programIR) {
        translateStaticDataSection(programIR);

        writer.write(".text\n");
        for (FunctionIR functionIR: programIR.getFunctions()) {
            translateFunction(functionIR, programIR);
        }
        writer.write("""

printi:
li $v0, 1
syscall
jr $ra

printf:
li $v0, 2
syscall
jr $ra


not:
beq $a0, $zero, not0
not1:
move $v0, $zero
jr $ra
not0:
li $v0, 1
jr $ra


exit:
li $v0, 17
syscall
jr $ra
""");
    }

    private void translateStaticDataSection(ProgramIR programIR) {
        writer.write(".data\n");
        for (BackendVariable staticVar : programIR.getStaticVariables()) {
            String description = "";
            if (staticVar.typeStructure.isArray()){
                description = String.format(".space %s", staticVar.sizeInBytes());
            } else if (staticVar.typeStructure.base == BaseType.INT) {
                description = ".word 0";
            } else if (staticVar.typeStructure.base == BaseType.FLOAT) {
                description = ".float 0.0";
            }
            writer.write(String.format("%s: %s\n", staticVar.staticName(), description));
        }
    }


    public void translateFunction(FunctionIR functionIR, ProgramIR programIR) {
        if (functionIR.getFunctionName().equals("main")) {
            writer.write(".globl main\n");
        }
        writer.write(functionIR.getFunctionName() + ":\n");

        // space to store old fp
        writer.write(String.format("addiu $sp, $sp, -%d\n", WORD_SIZE));
        writer.write("sw $fp, 0($sp)\n");
        writer.write("move $fp, $sp\n");

        // ======================== STACK LAYOUT ====================================================
        //         sp                                     fp
        //         V                                       V
        //         |          stack frame                  | old fp | ra  | stackarg0 | stackarg1 ...
        //  sizes: |           spOffset                    |   4    |  4  |     4     |   4       ...
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
        handleSaveRegData(saveRegOffset, "sw", "s.s");
        writer.write(String.format("sw $ra, %d($fp)\n", -spOffset));

        // ARGUMENT HANDLING:
        {
            ArgumentRegisterAllocator argumentRegisterAllocator = new ArgumentRegisterAllocator();
            int stackArgCounter = 0;
            for (BackendVariable argument : functionIR.getArguments()) {
                // we can reset temp allocation on each copy
                TemporaryRegisterAllocator temporaryRegisterAllocator = new TemporaryRegisterAllocator();

                String sourceReg = argumentRegisterAllocator.popArgOfType(argument.typeStructure.base);
                LoadedVariable target = new LoadedVariable(argument, temporaryRegisterAllocator, argument.typeStructure.base);


                if (sourceReg == null) {
                    // arg is in stack
                    String loadInstr = switch (argument.typeStructure.base) {
                        case INT -> "lw";
                        case FLOAT -> "l.s";
                    };
                    writer.write(String.format("%s %s, %d($fp)\n", loadInstr, target.getRegister(), 2 * WORD_SIZE + WORD_SIZE * stackArgCounter));
                    stackArgCounter += 1;
                } else {
                    // arg is in sourceReg
                    String moveInstr = switch (argument.typeStructure.base) {
                        case INT -> "move";
                        case FLOAT -> "mov.s";
                    };
                    writer.write(String.format("%s %s, %s\n", moveInstr, target.getRegister(), sourceReg));
                }
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

                            writer.write(String.format("move %s, %s\n", aRegister, bRegister));
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
                                writer.write(String.format("%s %s, %d(%s)", loadInstruction, copyRegister, WORD_SIZE * i, XRegister));
                                writer.write(String.format("%s %s, %d(%s)", storeInstruction, copyRegister, WORD_SIZE * i, YRegister));
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
                            BackendVariable retVar = functionIR.fetchVariableByName(returnVarName);
                            String retVarRegister = retVar.getAssignedRegister();
                            if (functionIR.getReturnType() == BaseType.INT) {
                                writer.write(String.format("move, $v0, %s\n", retVarRegister));
                            } else {
                                writer.write(String.format("move, $f0, %s\n", retVarRegister));
                            }
                        }
                        handleSaveRegData(saveRegOffset, "lw", "l.s");
                        writer.write(String.format("lw $ra, %d($fp)\n", -spOffset));
                        writer.write(String.format("addiu $sp, $sp, %d\n", spOffset));

                        // restore old fp
                        writer.write("lw $fp, 0($sp)\n");
                        // remove space allocated for storing old fp
                        writer.write(String.format("addiu $sp, $sp, -%d\n", WORD_SIZE));

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
                        FunctionIR callingFunction = programIR.getFunctionByName(callingFunctionName);
//                        ArrayList<BackendVariable> arguments = new ArrayList<BackendVariable>();

                        i += 1;
                        int stackVarIdx = 0;
                        assert instr.size() == i + callingFunction.getArguments().size();
                        for (int argIdx = 0; argIdx < callingFunction.getArguments().size(); argIdx++) {
                            // we can reset temp allocation on each copy
                            TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();

                            String argName = instr.getIthCode(i + argIdx);
                            BaseType argType = callingFunction.getArguments().get(argIdx).typeStructure.base;
                            LoadedVariable arg = new LoadedVariable(argName, functionIR, tempRegisterAllocator, argType);
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
                                writer.write(String.format("%s $%s, %d($fp)\n", storeInstr, arg.getRegister(), -spOffset - stackVarIdx * 4));
                            } else {
                                writer.write(String.format("%s $%s, $%s\n", asmInstr, argRegister, arg.getRegister()));
                            }
                        }
                        writer.write(String.format("jal %s:\n", callingFunctionName));

                        if (instr.getIthCode(0).equals("callr")) {
                            TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();

                            BaseType flushVarType = functionIR.fetchVariableByName(flushVarName).typeStructure.base;
                            LoadedVariable flushVar = new LoadedVariable(flushVarName, functionIR, tempRegisterAllocator, flushVarType);
                            String returnedValueRegister = "";
                            if (flushVarType == BaseType.INT) {
                                returnedValueRegister = "$v0";
                            } else {
                                returnedValueRegister = "$f0";
                            }
                            if (callingFunction.getReturnType() == BaseType.INT && flushVarType == BaseType.FLOAT) {
                                writer.write("mtc1 $v0, $f0\n");
                                writer.write("cvt.s.w $f0, $f0\n");
                            }
                            writer.write(String.format("move %s, %s\n", flushVar.getRegister(), returnedValueRegister));
                            writer.write(flushVar.flushAssembly());
                        }

                    }
                    case ARRAYSTORE -> {
                        ArrStoreLoadData arrData = getDataForArrayStoreLoadTranslation(instr, functionIR);
                        writer.write(arrData.a.loadAssembly());
                        writer.write(String.format("sw %s, 0(%s)\n", arrData.a.getRegister(), arrData.arrAddressRegister));
                    }
                    case ARRAYLOAD -> {
                        ArrStoreLoadData arrData = getDataForArrayStoreLoadTranslation(instr, functionIR);
                        writer.write(String.format("lw %s, 0(%s)\n", arrData.a.getRegister(), arrData.arrAddressRegister));
                        writer.write(arrData.a.flushAssembly());
                    }
                }

            } else if (iRentry.isLabel()) {
                writer.write(String.format("%s:\n", iRentry.asLabel().getName()));
            }
        }
    }

    private void translateBinaryOperation(String binop, IRInstruction instr, FunctionIR functionIR) {
        writer.write("move $fp, $sp\n");

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
        BaseType aType = functionIR.fetchVariableByName(aName).typeStructure.base;
        BaseType bType = functionIR.fetchVariableByName(bName).typeStructure.base;
        LoadedVariable a = new LoadedVariable(aName, functionIR, tempRegisterAllocator, aType);
        LoadedVariable b = new LoadedVariable(bName, functionIR, tempRegisterAllocator, bType);
        writer.write(a.loadAssembly());
        writer.write(b.loadAssembly());
        if (aType == BaseType.INT) {
            writer.write(String.format("%s %s, %s, %s\n", asmIntBranchOp.get(irBranchOp), a.getRegister(), b.getRegister(), label));
        } else {
            writer.write(String.format("%s %s, %s\n", asmFloatBranchOp.get(irBranchOp), a.getRegister(), b.getRegister()));
            writer.write(String.format("bc1t %s\n", label));
        }
    }

    private ArrStoreLoadData getDataForArrayStoreLoadTranslation(IRInstruction instr, FunctionIR functionIR) {
        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();

        String arrName = instr.getIthCode(1);
        BackendVariable arr = functionIR.fetchVariableByName(arrName);
        String iName = instr.getIthCode(2);
        LoadedVariable i = new LoadedVariable(iName, functionIR, tempRegisterAllocator, BaseType.INT);

        // TODO: possibly optimize to work with one less register

        writer.write(String.format("sll %s, %s, 2\n", i.getRegister(), i.getRegister()));
        String arrayBeginningRegister = loadArrayBeginning(tempRegisterAllocator, arr);
        writer.write(String.format("add %s, %s, %s\n", i.getRegister(), i.getRegister(), arrayBeginningRegister));

        String aName = instr.getIthCode(3);
        LoadedVariable a = new LoadedVariable(aName, functionIR, tempRegisterAllocator, arr.typeStructure.base);

        return new ArrStoreLoadData(a, i.getRegister());
    }
}

// for a := arr[i] or arr[i] := a
class ArrStoreLoadData {
    LoadedVariable a;
    String arrAddressRegister;

    public ArrStoreLoadData(LoadedVariable a, String arrAddressRegister) {
        this.a = a;
        this.arrAddressRegister = arrAddressRegister;
    }
}



















