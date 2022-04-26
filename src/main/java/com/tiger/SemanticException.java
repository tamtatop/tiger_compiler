package com.tiger;

import org.antlr.v4.runtime.Token;

class SemanticException extends Exception {
    int line_number;
    int column_number;
    String message;

    public SemanticException(String message, Token problematicToken) {
        super(message);
        line_number = problematicToken.getLine();
        column_number = problematicToken.getCharPositionInLine();
        this.message = message;
    }
}
