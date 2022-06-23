package com.tiger.ir;

import com.tiger.backend.BackendVariable;
import com.tiger.NakedVariable;
import com.tiger.ir.interfaces.*;
import com.tiger.types.BaseType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class ProgramIRBuilder implements IrGeneratorListener {


    private static class Function implements FunctionIR {
        BaseType returnType;
        String name;
        HashMap<String, BackendVariable> locals = new HashMap<>();
        List<BackendVariable> args;
        List<IRentry> entries;
        ProgramIR programIR;

        public Function(String name, List<BackendVariable> locals, List<BackendVariable> args, List<IRentry> entries, ProgramIR program, BaseType returnType) {
            this.name = name;
            for (BackendVariable local : locals) {
                this.locals.put(local.name, local);
            }
            this.args = args;
            this.entries = entries;
            this.programIR = program;
            this.returnType = returnType;
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
        public BaseType getReturnType() {
            return this.returnType;
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
        HashMap<String, BackendVariable> statics = new HashMap<>();
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

    private static boolean isVar(String str) {
        if(str==null){
            return false;
        }
        try {
            Double.parseDouble(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private static class Instruction implements IRInstruction, IRentry {
        String op;
        List<String> args;

        public Instruction(String op, List<String> args) {
            this.op = op;
            this.args = args.stream().filter(p -> !p.equals("")).toList();
        }

        @Override
        public List<String> reads() {
            String a = this.getIthCode(1);
            String b = this.getIthCode(2);
            String c = this.getIthCode(3);
            return switch (this.getType()) {
                case BINOP, BRANCH -> {
                    ArrayList<String> r = new ArrayList<>();
                    if(isVar(a)) {
                        r.add(a);
                    }
                    if(isVar(b)) {
                        r.add(b);
                    }
                    yield r;
                }
                case GOTO -> List.of();
                case RETURN -> isVar(a) ? List.of(a) : List.of();
                case CALL -> {
                    ArrayList<String> r = new ArrayList<>();
                    for (int i = this.op.equals("call") ? 2 : 3; i < args.size(); i++) {
                        if(isVar(getIthCode(i))) {
                            r.add(getIthCode(i));
                        }
                    }
                    yield r;
                }
                case ASSIGN, ARRAYSTORE, ARRAYLOAD -> {
                    ArrayList<String> r = new ArrayList<>();
                    if(isVar(b)) {
                        r.add(b);
                    }
                    if(isVar(c)) {
                        r.add(c);
                    }
                    yield r;
                }
            };
        }

        @Override
        public List<String> writes() {
            String a = this.getIthCode(1);
            String b = this.getIthCode(2);
            String c = this.getIthCode(3);
            return switch (this.getType()) {
                case BINOP -> {
                    assert c != null;
                    yield List.of(c);
                }
                case RETURN, BRANCH, GOTO -> List.of();
                case CALL -> {
                    if (this.op.equals("callr")) {
                        assert a != null;
                        yield List.of(a);
                    } else {
                        yield List.of();
                    }
                }
                case ASSIGN, ARRAYSTORE, ARRAYLOAD -> {
                    assert a != null;
                    yield List.of(a);
                }
            };
        }

        @Override
        public int size() {
            return args.size();
        }

        /**
         * add, x, y, z
         * 0 -> add
         * 1 -> x
         * 2 -> y
         * 3 -> z
         */
        @Override
        public String getIthCode(int i) {
            if(i<args.size()) {
                return args.get(i);
            } else {
                return null;
            }
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
            if ("call".equals(op) || "callr".equals(op)) {
                return IRInstructionType.CALL;
            }
            if ("array_store".equals(op)) {
                return IRInstructionType.ARRAYSTORE;
            }
            if ("array_load".equals(op)) {
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

        @Override
        public String toString() {
            return String.join(", ", args);
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

        @Override
        public String toString() {
            return String.format("%s:", name);
        }
    }

    private Program program;

    @Override
    public void genFunction(String functionName, List<NakedVariable> localVariables, List<NakedVariable> arguments, String irBody, BaseType returnType) {

        ArrayList<IRentry> entries = new ArrayList<>();
        irBody.lines().forEach(line -> {
            line = line.trim();
            if (line.length() == 0) return;
            if (!line.contains(":")) {
                int opEndIdx = line.indexOf(",");
                String op = line.substring(0, opEndIdx).trim();
                List<String> args = Arrays.stream(line.trim().split(",")).map(String::trim).toList();
                entries.add(new Instruction(op, args));
            } else {
                String label = line.substring(0, line.length() - 1);
                entries.add(new Label(label));
            }
        });
        List<BackendVariable> lvars = localVariables.stream().map(v -> new BackendVariable(v, false)).toList();
        List<BackendVariable> args = arguments.stream().map(v -> new BackendVariable(v, false)).toList();
        this.program.functions.put(functionName, new Function(functionName, lvars, args, entries, this.program, returnType));
    }

    @Override
    public void genProgram(String programName, List<NakedVariable> staticVariables) {
        this.program = new Program(programName, staticVariables.stream().map(v -> new BackendVariable(v, true)).toList());
    }

    public ProgramIR getProgramIR() {
        return this.program;
    }
}
