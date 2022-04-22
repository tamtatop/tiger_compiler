package com.tiger;

class TigerArgs {
    public String inputFilename;
    public String lexerFilename;
    public String parserFilename;
    public String symbolTableFilename;
    public String irFilename;


    public TigerArgs(String[] args) {
        int i = 0;
        boolean lexerOut = false;
        boolean parserOut = false;
        boolean symbolTableOut = false;
        boolean irOut = false;

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
            }
        }

        if(i!=args.length || this.inputFilename == null){
            System.err.println("Usage: <program> -i filename [-l] [-p] [--st] [--ir]");
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
        System.out.println("input fname:" + this.inputFilename);
    }
}
