package oop.ex6.Exceptions.FunctionExceptions;

import oop.ex6.Exceptions.JavacException;

public class FunctionException extends JavacException {
    private static final String FUNCTION_EXCEPTION_ADD_ON = "FunctionException -> ";
    public FunctionException(String msg) {
        super(FUNCTION_EXCEPTION_ADD_ON + msg);
    }
}
