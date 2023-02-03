package oop.ex6.main.FunctionExceptions;

public class CallParametersAmountException extends FunctionException {
    private static final String WRONG_PARAMS_ERR_MSG = "CallParametersAmountException:\nTried to call" +
            " a function with wrong number of parameters in: ";

    public CallParametersAmountException(String line) {
        super(WRONG_PARAMS_ERR_MSG + line);
    }
}
