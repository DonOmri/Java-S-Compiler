package oop.ex6.Exceptions.FunctionExceptions;

public class UnendingFunctionException extends FunctionException {
    private static final String ERR_MSG_PRE = "UnendingFunctionException:\nFunction that started in line ";
    private static final String ERR_MSG_POST = " Never Ended.";

    public UnendingFunctionException(int lineNumber){
        super(ERR_MSG_PRE + lineNumber + ERR_MSG_POST);
    }
}
