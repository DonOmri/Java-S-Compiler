package oop.ex6.main.VariableExceptions;

public class UninitializedVariableUseException extends VariableException{
    private static final String BAD_VAR_USE_ERR_MSG = "UninitializedVariableUseException: Tried to use the" +
            " uninitialized variable ";
    public UninitializedVariableUseException(String uninitialized) {
        super(BAD_VAR_USE_ERR_MSG + uninitialized);
    }
}
