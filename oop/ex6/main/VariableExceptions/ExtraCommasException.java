package oop.ex6.main.VariableExceptions;

public class ExtraCommasException extends VariableException{
    private static final String EXTRA_COMMA_ERR_MSG = "ExtraCommaException:\nthere is an extra comma at ";

    public ExtraCommasException(String var) {
        super(EXTRA_COMMA_ERR_MSG + var);
    }
}
