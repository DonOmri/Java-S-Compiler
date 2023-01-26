package oop.ex6.Exceptions;

public class TypeValueMismatchException extends VariableException{
    private static final String TypeValueMismatchMsg = "could not assign value to variable.\n(variable is ";

    public TypeValueMismatchException(String[] varFragments){
        super(TypeValueMismatchMsg + varFragments[0] + ", value is " + varFragments[1] + ")");
    }
}
