package com.tiger.ir;

import com.tiger.BackendVariable;
import com.tiger.NakedVariable;
import com.tiger.ir.interfaces.*;

import java.util.*;


public class ProgramIRBuilder implements IrGeneratorListener {


    private static class Function implements FunctionIR {
        String name;
        HashMap<String, BackendVariable> locals = new HashMap<>();
        List<BackendVariable> args;
        List<IRentry> entries;
        ProgramIR programIR;

        public Function(String name, List<BackendVariable> locals, List<BackendVariable> args, List<IRentry> entries, ProgramIR program) {
            this.name = name;
            for (BackendVariable local : locals) {
                this.locals.put(local.name, local);
            }
            this.args = args;
            this.entries = entries;
            this.programIR = program;
        }

        @Override
        public String getFunctionName() {
            return name;
        }

        @Override
        public List<BackendVariable> getLocalVariables() {
            return locals.values().stream().toList();
        }

        @Override
        public List<BackendVariable> getArguments() {
            return args;
        }

        @Override
        public List<IRentry> getBody() {
            return entries;
        }

        @Override
        public BackendVariable fetchVariableByName(String name) {
            return locals.get(name) != null ? locals.get(name) : programIR.getVariableByName(name);
        }
    }

    private static class Program implements ProgramIR {
        String name;
        HashMap<String, BackendVariable> statics  = new HashMap<>();
        HashMap<String, FunctionIR> functions = new HashMap<>();

        public Program(String name, List<BackendVariable> statics) {
            this.name = name;
            for (BackendVariable aStatic : statics) {
                this.statics.put(aStatic.name, aStatic);
            }
        }

        @Override
        public String getProgramName() {
            return name;
        }

        @Override
        public List<BackendVariable> getStaticVariables() {
            return statics.values().stream().toList();
        }

        @Override
        public BackendVariable getVariableByName(String name) {
            return statics.get(name);
        }

        @Override
        public List<FunctionIR> getFunctions() {
            return functions.values().stream().toList();
        }

        @Override
        public FunctionIR getFunctionByName(String name) {
            return functions.get(name);
        }
    }

    private static class Instruction implements IRInstruction, IRentry {
        String op;
        List<String> args;

        public Instruction(String op, List<String> args) {
            this.op = op;
            this.args = args;
        }

        @Override
        public List<String> reads() {
            return null;
        }

        @Override
        public List<String> writes() {
            return null;
        }

        @Override
        public String getIthCode(int i) {
            return null;
        }

        @Override
        public IRInstructionType getType() {
            if ("assign".equals(op)) {
                return IRInstructionType.ASSIGN;
            }
            List<String> binOps = List.of("add", "sub", "mult", "div", "and", "or");
            if (binOps.contains(op)) {
                return IRInstructionType.BINOP;
            }
            if ("goto".equals(op)) {
                return IRInstructionType.GOTO;
            }
            List<String> branches = List.of("breq", "brneq", "brlt", "brgt", "brleq", "brgeq");
            if (branches.contains(op)) {
                return IRInstructionType.BRANCH;
            }
            if ("return".equals(op)) {
                return IRInstructionType.RETURN;
            }
            if ("call".equals(op)) {
                return IRInstructionType.CALL;
            }
            if("array_store".equals(op)){
                return IRInstructionType.ARRAYSTORE;
            }
            if("array_load".equals(op)){
                return IRInstructionType.ARRAYLOAD;
            }
            throw new IllegalStateException("unknown op");
        }

        @Override
        public boolean isLabel() {
            return false;
        }

        @Override
        public boolean isInstruction() {
            return true;
        }

        @Override
        public IRInstruction asInstruction() {
            return this;
        }

        @Override
        public IRLabel asLabel() {
            return null;
        }
    }

    private static class Label implements IRLabel, IRentry {

        private final String name;

        private Label(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isLabel() {
            return true;
        }

        @Override
        public boolean isInstruction() {
            return false;
        }

        @Override
        public IRInstruction asInstruction() {
            return null;
        }

        @Override
        public IRLabel asLabel() {
            return this;
        }
    }

    private Program program;

    @Override
    public void genFunction(String functionName, List<NakedVariable> localVariables, List<NakedVariable> arguments, String irBody) {

        ArrayList<IRentry> entries = new ArrayList<>();
        irBody.lines().forEach(line -> {
            line = line.trim();
            if (line.length() == 0) return;
            if (!line.contains(":")) {
                int opEndIdx = line.indexOf(",");
                String op = line.substring(0, opEndIdx);
                List<String> args = Arrays.stream(line.substring(opEndIdx + 1).trim().split(",")).map(String::trim).toList();
                entries.add(new Instruction(op, args));
            } else {
                String label = line.substring(0, line.length()-1);
                entries.add(new Label(label));
            }
        });
        List<BackendVariable> lvars = localVariables.stream().map(v -> new BackendVariable(v, false)).toList();
        List<BackendVariable> args = arguments.stream().map(v -> new BackendVariable(v, false)).toList();
        this.program.functions.put(functionName, new Function(functionName, lvars, args, entries, this.program));
    }

    @Override
    public void genProgram(String programName, List<NakedVariable> staticVariables) {
        this.program = new Program(programName, staticVariables.stream().map(v -> new BackendVariable(v, true)).toList());
    }

    public ProgramIR getProgramIR() {
        return this.program;
    }
}
