package oop.ex6.Exceptions.FunctionExceptions;

public class InnerFunctionDeclarationException extends FunctionException{
    private static final String INNER_FUNC_DECLARATION_ERR_MSG = "InnerFunctionDeclarationException: An" +
            " inner function was declared at line ";

    public InnerFunctionDeclarationException(int line) {
        super(INNER_FUNC_DECLARATION_ERR_MSG + line);
    }
}
