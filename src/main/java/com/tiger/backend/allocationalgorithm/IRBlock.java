package com.tiger.backend.allocationalgorithm;

import com.tiger.ir.interfaces.IRentry;

import java.util.ArrayList;
import java.util.List;

public class IRBlock {
    public int blockIdx;
    public List<IRentry> entries;
    public ArrayList<Integer> neighbours;

    public IRBlock(int blockIdx, List<IRentry> entries) {
        this.blockIdx = blockIdx;
        this.entries = entries;
    }
}
