package oop.ex6.Exceptions;

public class UnendingFunctionException extends Exception{
    private static final String ERR_MSG_PRE = "Function that started in line ";
    private static final String ERR_MSG_POST = " Never Ended.";

    public UnendingFunctionException(int lineNumber){
        super(ERR_MSG_PRE + lineNumber + ERR_MSG_POST);
    }
}
