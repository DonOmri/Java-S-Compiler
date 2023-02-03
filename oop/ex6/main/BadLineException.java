package oop.ex6.main;

public class BadLineException extends SjavacException{
    private static final String BAD_LINE_MSG = "BadLineException: LineParser could not recognize the " +
            "following text: ";

    public BadLineException(String line){
        super(BAD_LINE_MSG + line);
    }
}
