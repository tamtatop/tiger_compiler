package com.tiger;

import com.tiger.antlr.TigerLexer;
import com.tiger.antlr.TigerParser;
import com.tiger.backend.LoadedVariable;
import com.tiger.backend.TemporaryRegisterAllocator;
import com.tiger.io.CancellableWriter;
import com.tiger.io.IOUtils;
import com.tiger.ir.ProgramIRBuilder;
import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.ir.interfaces.IRInstruction;
import com.tiger.ir.interfaces.IRentry;
import com.tiger.ir.interfaces.ProgramIR;
import com.tiger.types.TypeStructure;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;


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

    public MIPSGenerator(CancellableWriter writer) {
        this.writer = writer;
    }

    public void translateBinaryOperation(String binop, IRInstruction instr, FunctionIR functionIR) {
        TemporaryRegisterAllocator tempRegisterAllocator = new TemporaryRegisterAllocator();
        String aName = instr.getIthCode(1);
        String bName = instr.getIthCode(2);
        String cName = instr.getIthCode(3);
        BackendVariable a = functionIR.fetchVariableByName(aName);
        BackendVariable b = functionIR.fetchVariableByName(bName);
        BackendVariable c = functionIR.fetchVariableByName(cName);

        // TODO: handle immediate binops eg: addi
        // TODO: handle floats in ops eg: add.s

        LoadedVariable aLoaded = new LoadedVariable(a, tempRegisterAllocator);
        String aRegister = aLoaded.getRegister();
        writer.write(aLoaded.loadAssembly());

        LoadedVariable bLoaded = new LoadedVariable(b, tempRegisterAllocator);
        String bRegister = bLoaded.getRegister();
        writer.write(bLoaded.loadAssembly());

        LoadedVariable cLoaded = new LoadedVariable(c, tempRegisterAllocator);
        String cRegister = cLoaded.getRegister();

        if (!c.typeStructure.isBaseInt()) { binop += ".s"; }

        writer.write(String.format("%s %s, %s, %s", binop, cRegister, aRegister, bRegister));
        writer.write(cLoaded.flushAssembly());
    }

    public void translateFunction(FunctionIR functionIR) throws BackendException {
        functionIR.getBody();

        int spOffset = 0;
        for (BackendVariable localVariable : functionIR.getLocalVariables()) {
            if (localVariable.isSpilled) {
                localVariable.stackOffset = spOffset;
                spOffset = localVariable.sizeInBytes();
            }
        }
        // for $ra
        spOffset += 4;

        writer.write(String.format("addiu $sp, $sp, -%d\n", spOffset));
        for (BackendVariable localVariable : functionIR.getLocalVariables()) {
            if (localVariable.isSpilled) {
                writer.write(String.format("sw $, %d($sp)\n", localVariable.stackOffset));
            }
        }
        writer.write(String.format("sw $ra, %d($sp)\n", spOffset - 4));

        for (IRentry iRentry : functionIR.getBody()) {
            if (iRentry.isInstruction()) {
                IRInstruction instr = iRentry.asInstruction();
                switch (instr.getType()) {
                    case ASSIGN -> {

                    }
                    case BINOP -> {
                        switch (instr.getIthCode(0)) {
                            case "add" -> {
                                translateBinaryOperation("add", instr, functionIR);
                            }
                            case "sub" -> {
                                translateBinaryOperation("sub", instr, functionIR);
                            }
                            case "mult" -> {
                                translateBinaryOperation("mul", instr, functionIR);
                            }
                            case "div" -> {
                                translateBinaryOperation("div", instr, functionIR);
                            }
                            case "and" -> {
                                translateBinaryOperation("and", instr, functionIR);
                            }
                            case "or" -> {
                                translateBinaryOperation("or", instr, functionIR);
                            }
                        }
                    }
                    case GOTO -> {
                    }
                    case BRANCH -> {
                    }
                    case RETURN -> {
                        String returnVarName = instr.getIthCode(1);
                        if (!returnVarName.equals("")) {
                            BackendVariable retVar = functionIR.fetchVariableByName(returnVarName);
                            String retVarRegister = retVar.getAssignedRegister();
                            writer.write(String.format("move, $v0, %s\n", retVarRegister));
                        }

                        writer.write(String.format("lw $ra, %d($sp)\n", spOffset - 4));
                        writer.write(String.format("addiu $sp, $sp, %d\n", spOffset));
                    }
                    case CALL -> {
                    }
                    case ARRAYSTORE -> {
                    }
                    case ARRAYLOAD -> {
                    }
                }

            } else if(iRentry.isLabel()) {

            }
        }
    }
}




















