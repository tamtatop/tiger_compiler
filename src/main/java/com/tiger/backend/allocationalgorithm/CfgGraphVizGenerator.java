package com.tiger.backend.allocationalgorithm;

import com.tiger.io.CancellableWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CfgGraphVizGenerator {
//    public static void generateCfgGraphViz(CancellableWriter writer, List<IRBlock> blocks) {
//        writer.write("digraph cfg {\n");
//
//        writer.write("}\n");
//    }
    public static void generateCfgInit(CancellableWriter writer) {
        writer.write("digraph cfg {\n");
    }

    public static void generateCfgEnd(CancellableWriter writer) {
        writer.write("}\n");
    }

    public static void generateCfgFunctionBlocks(CancellableWriter writer, List<IRBlock> blocks) {
        for (IRBlock block : blocks) {
            List<String> instructions = block.entries.stream().map(Objects::toString).toList();
            String blockDesc = String.join("\n", instructions);
            writer.write(String.format("\n%s_%d [label = \"%s\", shape = \"rect\"]\n", block.functionName, block.blockIdx, blockDesc));

            List<String> blockNeibz = block.neighbours.stream().map(neighbourBlock -> String.format("%s_%d", neighbourBlock.functionName, neighbourBlock.blockIdx)).toList();
            System.out.println("BLA: " + blockNeibz);
            String neighbours = String.format("{%s}",String.join(", ", blockNeibz));
            writer.write(String.format("%s_%d -> %s\n", block.functionName, block.blockIdx, neighbours));
        }
    }
}
