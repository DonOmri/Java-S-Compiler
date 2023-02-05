package oop.ex6.main.FunctionExceptions;

public class NoReturnException extends FunctionException{
    private static final String NO_RETURN_ERR_MSG = "NoReturnException: Should've been a return " +
            "statement in line ";

    public NoReturnException(int lineNum) {
        super(NO_RETURN_ERR_MSG + lineNum);
    }
}
