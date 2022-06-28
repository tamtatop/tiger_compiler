package com.tiger.backend.allocationalgorithm;

import com.tiger.backend.BackendVariable;
import com.tiger.io.CancellableWriter;
import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.ir.interfaces.IRentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class LivenessAnalysis {

    private static String concatWithCommas(Collection<String> words) {
        StringBuilder wordList = new StringBuilder();
        for (String word : words) {
            wordList.append(word).append(",");
        }
        if(wordList.length() == 0){
            return "";
        } else {
            return new String(wordList.deleteCharAt(wordList.length() - 1));
        }
    }

    public static void performLivenessAnalysis(FunctionIR functionIR, List<IRBlock> blocks, CancellableWriter livenessWriter) {
        for (IRBlock block : blocks) {
            block.liveIn = new ArrayList<>();
            block.liveOut = new ArrayList<>();
            for (int i = 0; i < block.entries.size(); i++) {
                block.liveIn.add(new HashSet<>());
                block.liveOut.add(new HashSet<>());
            }
        }
        boolean didChange = true;
        while(didChange) {
            didChange = false;
            for (IRBlock block : blocks) {
                for (int i = 0; i < block.entries.size(); i++) {
                    IRentry instruction = block.entries.get(i);
                    List<String> uevar;
                    List<String> varkill;
                    if (instruction.isInstruction()) {
                        uevar = instruction.asInstruction().reads();
                        varkill = instruction.asInstruction().writes();
                    } else {
                        uevar = new ArrayList<>();
                        varkill = new ArrayList<>();
                    }

                    HashSet<String> livein = block.liveIn.get(i);
                    HashSet<String> liveout = block.liveOut.get(i);
                    HashSet<String> newin = new HashSet<>();


                    newin.addAll(liveout);
                    varkill.forEach(newin::remove);
                    newin.addAll(uevar);

                    if(!newin.equals(livein)) {
                        didChange = true;
                    }
                    block.liveIn.set(i, newin);

                    HashSet<String> newout = new HashSet<>();
                    if (i == block.entries.size() - 1) {
                        for (IRBlock neighbour : block.neighbours) {
                            newout.addAll(neighbour.liveIn.get(0));
                        }
                    } else {
                        newout.addAll(block.liveIn.get(i + 1));
                    }
                    if(!newout.equals(liveout)) {
                        didChange = true;
                    }
                    block.liveOut.set(i, newout);
                }
            }
        }
        livenessWriter.write(String.format("function %s:\n", functionIR.getFunctionName()));
        for (IRBlock block : blocks) {
            for (int i = 0; i < block.entries.size(); i++) {
                int idx = block.startIdx + i;
                HashSet<String> livein = block.liveIn.get(i);
                HashSet<String> liveout = block.liveOut.get(i);
                livenessWriter.write(String.format("#%d: %s livein = {%s} liveout = {%s}\n", idx, block.entries.get(i).toString(), concatWithCommas(livein), concatWithCommas(liveout)));
                IRentry s = block.entries.get(i);
                if(s.isInstruction()) {
                    livenessWriter.write(concatWithCommas(s.asInstruction().reads()));
                    livenessWriter.write("\n");
                    livenessWriter.write(concatWithCommas(s.asInstruction().writes()));
                    livenessWriter.write("\n");
                }
            }
        }
        for (BackendVariable localVariable : functionIR.getLocalVariables()) {
            boolean[] uses = new boolean[functionIR.getBody().size()];
            for (IRBlock block : blocks) {
                for (int i = 0; i < block.entries.size(); i++) {
                    int idx = block.startIdx + i;
                    HashSet<String> livein = block.liveIn.get(i);
                    HashSet<String> liveout = block.liveOut.get(i);
                    uses[idx] |= livein.contains(localVariable.name);
                    uses[idx] |= liveout.contains(localVariable.name);
                }
            }
            localVariable.setLivenessBooleans(uses);
        }
    }
}
