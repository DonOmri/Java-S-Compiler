package oop.ex6.Exceptions.VariableExceptions;

public class NoVariableTypeException extends VariableException {
    private static final String noVariableType = "NoVariableTypeException:\nVariable type can be " +
            "boolean / double / int / String / char\nIt was ";

    public NoVariableTypeException(String badType){
        super(noVariableType + badType);
    }
}
