package oop.ex6.main.FunctionExceptions;

public class DoubleFunctionDeclarationException extends FunctionException {

    private static final String DOUBLE_DECLARATION_MSG = "DoubleFunctionDeclarationException:\nA function" +
            " with that name was already declared.\nFunction name: ";

    public DoubleFunctionDeclarationException(String name){
        super(DOUBLE_DECLARATION_MSG + name);
    }
}
