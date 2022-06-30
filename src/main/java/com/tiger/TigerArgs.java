package com.tiger;

import com.tiger.backend.allocationalgorithm.RegisterAllocationAlgorithm;

class TigerArgs {
    public String mipsDefaultFilename;
    public String inputFilename;
    public String lexerFilename;
    public String parserFilename;
    public String symbolTableFilename;
    public String irFilename;
    public RegisterAllocationAlgorithm allocationStrategy;
    public String livenessFilename;
    public String cfgFilename;
    public String mipsFilename;

    public TigerArgs(String[] args) {
        int i = 0;
        boolean lexerOut = false;
        boolean parserOut = false;
        boolean symbolTableOut = false;
        boolean mipsOut = false;
        boolean irOut = false;
        boolean cfgOut = false;
        boolean livenessOut = false;
        RegisterAllocationAlgorithm allocation = null;


        while (i < args.length && args[i].startsWith("-")) {
            String arg = args[i++];

            switch (arg) {
                case "-i" ->  // input file
                        inputFilename = args[i++];
                case "-r" ->  // ir input file. we don't need it bitches
                        i++;
                case "-l" ->  // lexer output
                        lexerOut = true;
                case "-p" ->  // parse tree output
                        parserOut = true;
                case "--st" ->  // symbol table output
                        symbolTableOut = true;
                case "--ir" ->  // parse tree output
                        irOut = true;
                case "--cfg" ->  // parse tree output
                        cfgOut = true;
                case "--liveness" ->  // parse tree output
                        livenessOut = true;
                case "-n" ->
                        allocation = RegisterAllocationAlgorithm.NAIVE;
                case "-b" ->
                        allocation = RegisterAllocationAlgorithm.INTRABLOCK;
                case "-g" ->
                        allocation = RegisterAllocationAlgorithm.BRIGGS;
                case "--mips" ->
                        mipsOut = true;
            }
        }

        if(i!=args.length || this.inputFilename == null){
            System.err.println("Usage: <program> -i filename [-l] [-p] [--st] [--ir] [--cfg] [--liveness] [-n] [-b] [-g] [--mips]");
            System.err.println("You must provide input file");
            System.exit(1);
        }
        String baseFilename = inputFilename.substring(0, inputFilename.lastIndexOf('.'));
        if(lexerOut) {
            this.lexerFilename = baseFilename + ".tokens";
        }
        if(parserOut) {
            this.parserFilename = baseFilename + ".gv";
        }
        if(symbolTableOut) {
            this.symbolTableFilename = baseFilename + ".st";
        }
        if(irOut) {
            this.irFilename = baseFilename + ".ir";
        }
        if(cfgOut) {
            this.cfgFilename = baseFilename + ".cfg.gv";
        }
        if(livenessOut) {
            this.livenessFilename = baseFilename + ".liveness";
        }
        if(mipsOut) {
            if (allocation == null) {
                // default allocation strategy
                allocation = RegisterAllocationAlgorithm.NAIVE;
            }
            switch (allocation) {
                case NAIVE -> {
                    this.mipsFilename = baseFilename + ".naive.s";
                }
                case INTRABLOCK -> {
                    this.mipsFilename = baseFilename + ".ib.s";
                }
                case BRIGGS -> {
                    this.mipsFilename = baseFilename + ".briggs.s";
                }
            }
            this.mipsDefaultFilename = baseFilename + ".s";
        }
        this.allocationStrategy = allocation;
        System.out.println("input fname:" + this.inputFilename);
    }
}
