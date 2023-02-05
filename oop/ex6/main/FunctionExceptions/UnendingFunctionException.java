package oop.ex6.main.FunctionExceptions;

public class UnendingFunctionException extends FunctionException {
    private static final String UNENDING_FUNC_ERR_MSG = "UnendingFunctionException: Function that started" +
            " in line %s Never ended";


    public UnendingFunctionException(int lineNumber){
        super(String.format(UNENDING_FUNC_ERR_MSG, lineNumber));
    }
}
