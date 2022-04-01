package com.tiger;

class TigerArgs {
    public String inputFilename;
    public String lexerFilename;
    public String parserFilename;


    public TigerArgs(String[] args) {
        int i = 0;

        while (i < args.length && args[i].startsWith("-")) {
            String arg = args[i++];
            String val = args[i++];

            switch (arg) {
                case "-i" ->  // input file
                        inputFilename = val;
                case "-l" ->  // lexer output file
                        lexerFilename = val;
                case "-p" ->  // parse tree output file
                        parserFilename = val;
            }
        }

        if(i!=args.length || this.inputFilename == null){
            System.err.println("Usage: <program> -i filename [-l lexerout] [-p parserout]");
            System.err.println("You must provide input file");
            System.exit(1);
        }
    }
}
