package oop.ex6.main.VariableExceptions;

public class UninitializedVariableUse extends VariableException{
    private static final String BAD_VAR_USE_ERR_MSG = "UninitializedVariableUse: Tried to initialize " +
            "variable %s with uninitialized variable %s";
    public UninitializedVariableUse(String assigned, String assignee) {
        super(String.format(BAD_VAR_USE_ERR_MSG, assigned, assignee));
    }
}
