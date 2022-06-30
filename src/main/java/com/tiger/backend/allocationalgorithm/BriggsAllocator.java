package com.tiger.backend.allocationalgorithm;

import com.tiger.backend.BackendVariable;
import com.tiger.ir.interfaces.FunctionIR;
import com.tiger.types.BaseType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class BriggsAllocator {

    private static class Node {
        int cost;
        ArrayList<Integer> neighbours = new ArrayList<>();
    }

    private static Node[] convertBackendsToNodes(List<BackendVariable> backendVariables) {
        int n = backendVariables.size();
        Node[] nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node();
            for (int j = 0; j < n; j++) {
                if (backendVariables.get(i).doesIntersect(backendVariables.get(j))) {
                    nodes[i].neighbours.add(j);
                }
            }
            nodes[i].cost = backendVariables.get(i).spillCost;
        }
        return nodes;
    }

    private static int getDeg(List<Integer> neighbours, boolean[] removed, int v) {
        int c = 0;
        for (int i : neighbours) {
            if (removed[i]) continue;
            c++;
        }
        return c;
    }

    // -1 = no color
    // colors: 0..n-1
    private static int[] doBriggsAlgorithm(Node[] G, int numColors) {
        int n = G.length;
        boolean[] removed = new boolean[n];

        int left = n;
        Stack<Integer> coloringStack = new Stack<>();


        while (left > 0) {
            int chosen = -1;
            for (int i = 0; i < n; i++) {
                if (removed[i]) continue;
                if (getDeg(G[i].neighbours, removed, i) < numColors) {
                    chosen = i;
                    break;
                }
            }
            if (chosen == -1) {
                int lowestCost = Integer.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    if (removed[i]) continue;
                    if (lowestCost >= G[i].cost) {
                        lowestCost = G[i].cost;
                        chosen = i;
                    }
                }
            }
            coloringStack.push(chosen);
            removed[chosen] = true;
            left--;
        }
        return colorNodesUsingStackOrder(G, coloringStack, numColors);
    }

    private static int[] colorNodesUsingStackOrder(Node[] G, Stack<Integer> coloringStack, int numColors) {
        int n = G.length;
        int[] colors = new int[n];
        boolean[] removed = new boolean[n];
        Arrays.fill(colors, -1);
        Arrays.fill(removed, true);
        while(!coloringStack.isEmpty()) {
            int top = coloringStack.pop();
            List<Integer> neighborColors = G[top].neighbours.stream().filter(i -> !removed[i]).map(i -> colors[i]).filter(c -> c!=-1).sorted().toList();
            int chosenColor = -1;
            int cur = 0;
            for (Integer neighborColor : neighborColors) {
                if (neighborColor == cur) {
                    cur += 1;
                }
            }
            if(cur < numColors){
                chosenColor = cur;
            }
            assert chosenColor < numColors;
            colors[top] = chosenColor;
            removed[top] = false;
        }
        return colors;
    }


    public static void assignRegisters(FunctionIR functionIR, List<IRBlock> blocks, String[] intSaves, String[] floatSaves) {
        // allocate int regs
        List<BackendVariable> intVars = functionIR.getLocalVariables().stream().filter(b -> !b.typeStructure.isArray()).filter(b -> b.typeStructure.base == BaseType.INT).toList();
        assignRegistersToSingleTypeOfVars(intSaves, intVars);

        // allocate float regs
        List<BackendVariable> floatVars = functionIR.getLocalVariables().stream().filter(b -> !b.typeStructure.isArray()).filter(b -> b.typeStructure.base == BaseType.FLOAT).toList();
        assignRegistersToSingleTypeOfVars(floatSaves, floatVars);

        // Spill all arrays
        functionIR.getLocalVariables().stream().filter(b -> b.typeStructure.isArray()).toList().forEach(
                BackendVariable::spill
        );
    }

    private static void assignRegistersToSingleTypeOfVars(String[] registers, List<BackendVariable> variables) {
        Node[] nodes = convertBackendsToNodes(variables);
        int[] colors = doBriggsAlgorithm(nodes, registers.length);
        for (int i = 0; i < variables.size(); i++) {
            if(colors[i] == -1){
                variables.get(i).spill();
            } else {
                variables.get(i).assignRegister(registers[colors[i]]);
            }
        }
    }
}
