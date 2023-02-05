package oop.ex6.main.FunctionExceptions;

public class NoFunctionException extends FunctionException{
    private static final String NO_FUNC_ERR_MSG = "NoFunctionException: Name of called function could " +
            "not be recognized in: ";

    public NoFunctionException(String line) {
        super(NO_FUNC_ERR_MSG + line);
    }
}
