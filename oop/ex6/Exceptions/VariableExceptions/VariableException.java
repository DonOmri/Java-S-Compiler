package oop.ex6.Exceptions.VariableExceptions;

public class VariableException extends Exception{
    private static final String VAR_EXCEPTION_ADD_ON = "VariableException -> ";

    public VariableException(String msg){
        super(VAR_EXCEPTION_ADD_ON + msg);
    }
}
