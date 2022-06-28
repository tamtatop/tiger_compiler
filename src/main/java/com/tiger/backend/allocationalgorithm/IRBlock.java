package com.tiger.backend.allocationalgorithm;

import com.tiger.ir.interfaces.IRentry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class IRBlock {
    public int blockIdx;
    public int startIdx;
    public List<IRentry> entries;
    public ArrayList<IRBlock> neighbours;
    public String functionName;
    public ArrayList<HashSet<String>> liveIn;
    public ArrayList<HashSet<String>> liveOut;

    public IRBlock(int blockIdx, int startIdx, List<IRentry> entries, String functionName) {
        this.blockIdx = blockIdx;
        this.startIdx = startIdx;
        this.entries = entries;
        this.neighbours = new ArrayList<>();
        this.functionName = functionName;
    }
}
