package oop.ex6.Exceptions;

public class NoVariableTypeException extends VariableException{
    private static final String noType = "Variable type can be boolean/double/int/String/char\nIt was ";

    public NoVariableTypeException(String badType){
        super(noType + badType);
    }
}
