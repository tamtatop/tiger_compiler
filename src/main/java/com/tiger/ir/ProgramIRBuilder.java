package com.tiger.ir;

import com.tiger.BackendVariable;
import com.tiger.NakedVariable;
import com.tiger.ir.interfaces.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class ProgramIRBuilder implements IrGeneratorListener {
    private static class Function implements FunctionIR {
        String name;
        List<BackendVariable> locals;
        List<BackendVariable> args;
        List<IRentry> entries;

        public Function(String name, List<BackendVariable> locals, List<BackendVariable> args, List<IRentry> entries) {
            this.name = name;
            this.locals = locals;
            this.args = args;
            this.entries = entries;
        }

        @Override
        public String getFunctionName() {
            return name;
        }

        @Override
        public List<BackendVariable> getLocalVariables() {
            return locals;
        }

        @Override
        public List<BackendVariable> getArguments() {
            return args;
        }

        @Override
        public List<IRentry> getBody() {
            return entries;
        }
    }

    private static class Program implements ProgramIR {
        String name;
        List<BackendVariable> statics;
        List<FunctionIR> functions;

        public Program(String name, List<BackendVariable> statics, List<FunctionIR> functions) {
            this.name = name;
            this.statics = statics;
            this.functions = functions;
        }

        @Override
        public String getProgramName() {
            return name;
        }

        @Override
        public List<BackendVariable> getStaticVariables() {
            return statics;
        }

        @Override
        public List<FunctionIR> getFunctions() {
            return functions;
        }

        // TODO: use hashtable for this
        @Override
        public FunctionIR getFunctionByName(String name) {
            for (FunctionIR function : functions) {
                if (Objects.equals(function.getFunctionName(), name)) {
                    return function;
                }
            }
            return null;
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

    ArrayList<FunctionIR> functions = new ArrayList<>();
    String programName;
    List<NakedVariable> staticVariables;

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
                System.out.println(op);
                for (String arg : args) {
                    System.out.print(arg + " :: ");
                }
                System.out.println();
                System.out.println();
                entries.add(new Instruction(op, args));
            } else {
                System.out.println("label:");
                System.out.println(line);
                String label = line.substring(0, line.length()-1);
                entries.add(new Label(label));
            }
        });
        List<BackendVariable> lvars = localVariables.stream().map(BackendVariable::new).toList();
        List<BackendVariable> args = arguments.stream().map(BackendVariable::new).toList();
        functions.add(new Function(functionName, lvars, args, entries));
    }

    @Override
    public void genProgram(String programName, List<NakedVariable> staticVariables) {
        this.programName = programName;
        this.staticVariables = staticVariables;
    }

    public ProgramIR getProgramIR() {
        List<BackendVariable> statics = staticVariables.stream().map(BackendVariable::new).toList();
        return new Program(programName, statics, functions);
    }
}
