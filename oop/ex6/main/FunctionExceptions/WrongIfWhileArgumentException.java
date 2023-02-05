package oop.ex6.main.FunctionExceptions;

import oop.ex6.main.SjavacException;

public class WrongIfWhileArgumentException extends SjavacException {
    private static final String WRONG_IF_WHILE_ERR_MSG = "WrongIfWhileArgumentException: Cannot use a" +
            " String/char type variable in an if/while statements. Variable is ";

    public WrongIfWhileArgumentException(String varName) {
        super(WRONG_IF_WHILE_ERR_MSG + varName);
    }
}
