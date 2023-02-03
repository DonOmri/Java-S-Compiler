package oop.ex6.main;

public class SjavacException extends Exception{
    private static final String JAVAC_EXCEPTION_ADD_ON = "SJavacException -> ";
    public SjavacException(String msg){
        super(JAVAC_EXCEPTION_ADD_ON + msg);
    }
}
