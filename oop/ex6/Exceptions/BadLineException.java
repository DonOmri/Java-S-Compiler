package oop.ex6.Exceptions;

public class BadLineException extends Exception{
    private static final String BAD_LINE_MSG = "BadLineException: Global scope could not recognize the " +
            "following text: ";

    public BadLineException(String msg){
        super(BAD_LINE_MSG + msg);
    }
}
