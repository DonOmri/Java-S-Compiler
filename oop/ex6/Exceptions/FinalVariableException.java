package oop.ex6.Exceptions;

public class FinalVariableException extends VariableException{
    private static final String DECLARATION_ERR_MSG = "Could not declare a final variable without a value";
    private static final String ASSIGNMENT_ERR_MSG = "Could not assign a value to a final variable";

    public FinalVariableException(boolean atDeclaration){
        super(atDeclaration ? DECLARATION_ERR_MSG : ASSIGNMENT_ERR_MSG);
    }
}
