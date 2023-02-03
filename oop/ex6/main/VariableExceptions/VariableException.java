package oop.ex6.main.VariableExceptions;

import oop.ex6.main.SjavacException;

public class VariableException extends SjavacException {
    private static final String VAR_EXCEPTION_ADD_ON = "VariableException -> ";

    public VariableException(String msg){
        super(VAR_EXCEPTION_ADD_ON + msg);
    }
}
