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
import com.tiger.types.TypeStructure;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;


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

        if(errorLogger.anyError()) {
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

    public static void runBackend(ProgramIR ir,  CancellableWriter cfgWriter, CancellableWriter livenessWriter, CancellableWriter mipsWriter) {

    }

}

interface RegisterAllocator {

    void runAllocationAlgorithm(ProgramIR ir);

}

class NaiveRegisterAllocator implements RegisterAllocator{

    @Override
    public void runAllocationAlgorithm(ProgramIR ir) {
        for (FunctionIR functionIR: ir.getFunctions()) {
            for (BackendVariable localVariable: functionIR.getLocalVariables()){
                localVariable.spill();
            }
        }
    }
}

class MIPSGenerator {
    private final CancellableWriter writer;
    private static final HashMap<String, String> asmBinaryOp = new HashMap<>();
    private static final HashMap<String, String> asmBranchOp = new HashMap<>();

//    "breq" : "beq", "brneq" : "bne", "brlt" : "blt", "brgt" : "bgt", "brleq" : "ble", "brgeq" : "bge"

    static {
        asmBinaryOp.put("add", "add");
        asmBinaryOp.put("sub", "sub");
        asmBinaryOp.put("mult", "mul");
        asmBinaryOp.put("div", "div");
        asmBinaryOp.put("and", "and");
        asmBinaryOp.put("or", "or");

        asmBranchOp.put("breq", "beq");
        asmBranchOp.put("brneq", "bne");
        asmBranchOp.put("brlt", "blt");
        asmBranchOp.put("brgt", "bgt");
        asmBranchOp.put("brleq", "ble");
        asmBranchOp.put("brgeq", "bge");
    }
    public MIPSGenerator(CancellableWriter writer) {
        this.writer = writer;
    }

    public void translateBinaryOperation(String binop, IRInstruction instr, FunctionIR functionIR) {
        writer.write("move $fp, $sp\n");

        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();
        String aName = instr.getIthCode(1);
        String bName = instr.getIthCode(2);
        String cName = instr.getIthCode(3);
        BackendVariable c = functionIR.fetchVariableByName(cName);

        // TODO: handle immediate binops eg: addi
        // TODO: handle floats in ops eg: add.s

        LoadedVariable aLoaded = new LoadedVariable(aName, functionIR, tempRegisterAllocator, c.typeStructure.base);
        String aRegister = aLoaded.getRegister();
        writer.write(aLoaded.loadAssembly());

        LoadedVariable bLoaded = new LoadedVariable(bName, functionIR, tempRegisterAllocator, c.typeStructure.base);
        String bRegister = bLoaded.getRegister();
        writer.write(bLoaded.loadAssembly());

        LoadedVariable cLoaded = new LoadedVariable(cName, functionIR, tempRegisterAllocator, c.typeStructure.base);
        String cRegister = cLoaded.getRegister();

        if (!c.typeStructure.isBaseInt()) { binop += ".s"; }

        writer.write(String.format("%s %s, %s, %s\n", binop, cRegister, aRegister, bRegister));
        writer.write(cLoaded.flushAssembly());
    }

    public void translateFunction(FunctionIR functionIR, ProgramIR programIR) throws BackendException {
        writer.write(functionIR.getFunctionName() + ":");

        int spOffset = 0;
//        int fp
        for (BackendVariable localVariable : functionIR.getLocalVariables()) {
            if (localVariable.isSpilled) {
                spOffset += localVariable.sizeInBytes();
                    localVariable.stackOffset = -spOffset;
            }
        }
        // for $ra
        spOffset += 4;

        writer.write(String.format("addiu $sp, $sp, -%d\n", spOffset));
        for (BackendVariable localVariable : functionIR.getLocalVariables()) {
            if (localVariable.isSpilled) {
                writer.write(String.format("sw $, %d($fp)\n", localVariable.stackOffset));
            }
        }
        writer.write(String.format("sw $ra, %d($fp)\n", -spOffset));

        for (IRentry iRentry : functionIR.getBody()) {
            if (iRentry.isInstruction()) {
                IRInstruction instr = iRentry.asInstruction();
                switch (instr.getType()) {
                    case ASSIGN -> {
                        // a := b
                        // TODO: array assign
                        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();
                        String aName = instr.getIthCode(1);
                        String bName = instr.getIthCode(2);
                        BackendVariable a = functionIR.fetchVariableByName(aName);

                        LoadedVariable aLoaded = new LoadedVariable(aName, functionIR, tempRegisterAllocator, a.typeStructure.base);
                        String aRegister = aLoaded.getRegister();

                        LoadedVariable bLoaded = new LoadedVariable(bName, functionIR, tempRegisterAllocator, a.typeStructure.base);
                        String bRegister = bLoaded.getRegister();
                        writer.write(bLoaded.loadAssembly());

                        writer.write(String.format("move %s, %s\n", aRegister, bRegister));
                        writer.write(aLoaded.flushAssembly());

                    }
                    case BINOP -> {
                        translateBinaryOperation(asmBinaryOp.get(instr.getIthCode(0)), instr, functionIR);
                    }
                    case GOTO -> {
                        String afterLoop = instr.getIthCode(1);
                        writer.write(String.format("j %s", afterLoop));
                    }
                    case BRANCH -> {
                        translateBranchOperation(asmBranchOp.get(instr.getIthCode(0)), instr, functionIR);
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

                        writer.write(String.format("lw $ra, %d($fp)\n", -spOffset));
                        writer.write(String.format("addiu $sp, $sp, %d\n", spOffset));
                        writer.write("jr $ra\n");

                    }
                    case CALL -> {
                        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();
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
                        for (; i < instr.size(); i++) {
                            String argName = instr.getIthCode(i);
//                            arguments.add(functionIR.fetchVariableByName(argName));
                            BackendVariable argBackend = functionIR.fetchVariableByName(argName);
                            BaseType argType =  argBackend.typeStructure.base;
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
                                writer.write(String.format("%s $%s, %d($fp)\n", storeInstr, arg.getRegister(), -spOffset - stackVarIdx*4));
                            } else {
                                writer.write(String.format("%s $%s, $%s\n", asmInstr, argRegister, arg.getRegister()));
                            }
                        }
                        // TODO: jump tu generate new function or smthn. check if it's correct
                        writer.write(String.format("jal %s:", callingFunctionName));

                        if (instr.getIthCode(0).equals("callr")) {
                            BaseType flushVarType = functionIR.fetchVariableByName(flushVarName).typeStructure.base;
                            LoadedVariable flushVar = new LoadedVariable(flushVarName, functionIR, tempRegisterAllocator, flushVarType);
                            String returnedValueRegister = "";
                            if (flushVarType == BaseType.INT) {
                                returnedValueRegister = "$v0";
                            } else {
                                returnedValueRegister = "$f0";
                            }
                            if (callingFunction.getReturnType() == BaseType.INT && flushVarType == BaseType.FLOAT){
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
                        writer.write(String.format("sw %s, 0(%s)", arrData.a.getRegister(), arrData.arrAddressRegister));
                    }
                    case ARRAYLOAD -> {
                        ArrStoreLoadData arrData = getDataForArrayStoreLoadTranslation(instr, functionIR);
                        writer.write(String.format("lw %s, 0(%s)", arrData.a.getRegister(), arrData.arrAddressRegister));
                        writer.write(arrData.a.flushAssembly());
                    }
                }

            } else if(iRentry.isLabel()) {

            }
        }
    }

    private void translateBranchOperation(String branchOp, IRInstruction instr, FunctionIR functionIR) {
        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();
        String aName = instr.getIthCode(1);
        String bName = instr.getIthCode(2);
        String label = instr.getIthCode(3);
        LoadedVariable a = new LoadedVariable(aName, functionIR, tempRegisterAllocator, BaseType.INT);
        LoadedVariable b = new LoadedVariable(bName, functionIR, tempRegisterAllocator, BaseType.INT);
        writer.write(a.loadAssembly());
        writer.write(b.loadAssembly());
        writer.write(String.format("%s %s, %s, %s", branchOp, a.getRegister(), b.getRegister(), label));
    }

    private ArrStoreLoadData getDataForArrayStoreLoadTranslation(IRInstruction instr, FunctionIR functionIR){
        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();

        String arrName = instr.getIthCode(1);
        BackendVariable arr = functionIR.fetchVariableByName(arrName);
        String iName = instr.getIthCode(2);
        LoadedVariable i = new LoadedVariable(iName, functionIR, tempRegisterAllocator, BaseType.INT);

        writer.write(String.format("sll %s, %s, 2", i.getRegister(), i.getRegister()));
        writer.write(String.format("add %s, %s, $fp", i.getRegister(), i.getRegister()));
        writer.write(String.format("addi %s, %s, %s", i.getRegister(), i.getRegister(), arr.stackOffset));

        String aName = instr.getIthCode(3);
        LoadedVariable a = new LoadedVariable(aName ,functionIR, tempRegisterAllocator, arr.typeStructure.base);

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



















