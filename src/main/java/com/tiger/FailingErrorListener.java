package com.tiger;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class FailingErrorListener extends BaseErrorListener {

    private final String message;
    private final int status;

    public FailingErrorListener(String message, int status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
            throws ParseCancellationException {
        System.err.println(this.message);
        System.exit(this.status);
    }

}
