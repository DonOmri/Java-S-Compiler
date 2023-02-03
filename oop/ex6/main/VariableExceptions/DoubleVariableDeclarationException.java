package oop.ex6.main.VariableExceptions;

public class DoubleVariableDeclarationException extends VariableException {
    private static final String DOUBLE_DECLARATION_MSG = "DoubleDeclarationException:\nA variable with" +
            " that name is already declared in this scope.\nVariable name: ";

    public DoubleVariableDeclarationException(String varName){
        super(DOUBLE_DECLARATION_MSG + varName);
    }
}
