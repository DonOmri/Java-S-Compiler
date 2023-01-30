package oop.ex6.Exceptions;

public class JavacException extends Exception{
    private static final String JAVAC_EXCEPTION_ADD_ON = "JavacException -> ";
    public JavacException(String msg){
        super(JAVAC_EXCEPTION_ADD_ON + msg);
    }
}
