package com.tiger;

enum RegisterAllocation {
    NAIVE,
    INTRABLOCK,
    BRIGGS
}

class TigerArgs {
    public String inputFilename;
    public String lexerFilename;
    public String parserFilename;
    public String symbolTableFilename;
    public String irFilename;
    public RegisterAllocation allocationStrategy;
    public String livenessFilename;
    public String cfgFilename;

    public TigerArgs(String[] args) {
        int i = 0;
        boolean lexerOut = false;
        boolean parserOut = false;
        boolean symbolTableOut = false;
        boolean irOut = false;
        boolean cfgOut = false;
        boolean livenessOut = false;
        RegisterAllocation allocation = null;


        while (i < args.length && args[i].startsWith("-")) {
            String arg = args[i++];

            switch (arg) {
                case "-i" ->  // input file
                        inputFilename = args[i++];
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
                        allocation = RegisterAllocation.NAIVE;
                case "-b" ->
                        allocation = RegisterAllocation.INTRABLOCK;
                case "-g" ->
                        allocation = RegisterAllocation.BRIGGS;
            }
        }

        if(i!=args.length || this.inputFilename == null){
            System.err.println("Usage: <program> -i filename [-l] [-p] [--st] [--ir] [--cfg] [--liveness] [-n] [-b] [-g]");
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
            this.cfgFilename = baseFilename + ".cfg";
        }
        if(livenessOut) {
            this.livenessFilename = baseFilename + ".liveness";
        }
        this.allocationStrategy = allocation;
        System.out.println("input fname:" + this.inputFilename);
    }
}
