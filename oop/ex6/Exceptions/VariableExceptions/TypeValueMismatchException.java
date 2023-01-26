package oop.ex6.Exceptions.VariableExceptions;

import oop.ex6.Exceptions.VariableExceptions.VariableException;

public class TypeValueMismatchException extends VariableException {
    private static final String TypeValueMismatchMsg = "TypeValueMismatchException: could not assign " +
            "value to variable.\n(variable is ";

    public TypeValueMismatchException(String[] varFragments){
        super(TypeValueMismatchMsg + varFragments[0] + ", value is " + varFragments[1] + ")");
    }
}
