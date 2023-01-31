package oop.ex6.Exceptions.FunctionExceptions;

public class CallWrongParametersException extends FunctionException {
    private static final String WRONG_PARAMS_ERR_MSG = "Tried to call a function with wrong number of" +
            " parameters, or parameter types were incorrect in: ";

    public CallWrongParametersException(String line) {
        super(WRONG_PARAMS_ERR_MSG + line);
    }
}
