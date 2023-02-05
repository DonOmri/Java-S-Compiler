package oop.ex6.main.VariableExceptions;

public class NoVariableTypeException extends VariableException {
    private static final String noVariableType = "NoVariableTypeException: Variable type can be " +
            "boolean / double / int / String / char . It was ";

    public NoVariableTypeException(String badType){
        super(noVariableType + badType);
    }
}
