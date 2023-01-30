package oop.ex6.Exceptions.FunctionExceptions;

public class DoubleFunctionDeclarationException extends FunctionException {

    private static final String DOUBLE_DECLARATION_MSG = "DoubleFunctionDeclarationException: A function with" +
            " that name was already declared.\nFunction name: ";

    public DoubleFunctionDeclarationException(String name){
        super(DOUBLE_DECLARATION_MSG + name);
    }
}
