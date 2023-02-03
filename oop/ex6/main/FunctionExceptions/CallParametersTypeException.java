package oop.ex6.main.FunctionExceptions;

public class CallParametersTypeException extends FunctionException {
    private static final String WRONG_PARAMS_ERR_MSG = "CallParametersTypeException:\nTried to call a" +
            " function with wrong type of parameter in: ";

    public CallParametersTypeException(String line) {
        super(WRONG_PARAMS_ERR_MSG + line);
    }
}
