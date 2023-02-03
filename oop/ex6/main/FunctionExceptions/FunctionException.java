package oop.ex6.main.FunctionExceptions;

import oop.ex6.main.SjavacException;

public class FunctionException extends SjavacException {
    private static final String FUNCTION_EXCEPTION_ADD_ON = "FunctionException -> ";
    public FunctionException(String msg) {
        super(FUNCTION_EXCEPTION_ADD_ON + msg);
    }
}
