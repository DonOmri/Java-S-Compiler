package oop.ex6.Exceptions.VariableExceptions;

import oop.ex6.Exceptions.JavacException;

public class VariableException extends JavacException {
    private static final String VAR_EXCEPTION_ADD_ON = "VariableException -> ";

    public VariableException(String msg){
        super(VAR_EXCEPTION_ADD_ON + msg);
    }
}
