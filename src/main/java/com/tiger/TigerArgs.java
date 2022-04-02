package com.tiger;

class TigerArgs {
    public String inputFilename;
    public String lexerFilename;
    public String parserFilename;


    public TigerArgs(String[] args) {
        int i = 0;
        boolean lexerOut = false;
        boolean parserOut = false;

        while (i < args.length && args[i].startsWith("-")) {
            String arg = args[i++];

            switch (arg) {
                case "-i" ->  // input file
                        inputFilename = args[i++];
                case "-l" ->  // lexer output file
                        lexerOut = true;
                case "-p" ->  // parse tree output file
                        parserOut = true;
            }
        }

        if(i!=args.length || this.inputFilename == null){
            System.err.println("Usage: <program> -i filename [-l] [-p]");
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
        System.out.println("input fname:" + this.inputFilename);
    }
}
