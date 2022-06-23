package com.tiger.backend.allocationalgorithm;

import com.tiger.ir.interfaces.IRentry;

import java.util.ArrayList;
import java.util.List;

public class IRBlock {
    public int blockIdx;
    public List<IRentry> entries;
    public ArrayList<IRBlock> neighbours;
    public String functionName;

    public IRBlock(int blockIdx, List<IRentry> entries, String functionName) {
        this.blockIdx = blockIdx;
        this.entries = entries;
        this.neighbours = new ArrayList<>();
        this.functionName = functionName;
    }
}
