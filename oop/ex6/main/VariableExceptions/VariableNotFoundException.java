package oop.ex6.main.VariableExceptions;

public class VariableNotFoundException extends VariableException{
    private static final String VAR_NOT_FOUND_ERR_MSG = "VariableNotFoundException:\nThe referred" +
            " variable could not be found. Variable name: ";

    public VariableNotFoundException(String name) {
        super(VAR_NOT_FOUND_ERR_MSG + name);
    }
}
