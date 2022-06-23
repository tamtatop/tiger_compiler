package com.tiger.backend.allocationalgorithm;

import com.tiger.io.CancellableWriter;

import java.util.List;
import java.util.Objects;

public class CfgGraphVizGenerator {

    public static void generateCfgGraphViz(CancellableWriter writer, List<IRBlock> blocks) {
        writer.write("digraph cfg {\n");
        for (IRBlock block : blocks) {
            List<String> instructions = block.entries.stream().map(Objects::toString).toList();
            String blockDesc = String.join("", instructions);
            writer.write(String.format("%d [label = %s]\n", block.blockIdx, blockDesc));

            String neighbours = block.neighbours.toString().replace("[", "{");
            neighbours = neighbours.replace("]", "}");
            writer.write(String.format("%d -> %s\n", block.blockIdx, neighbours));
        }
    }
}
