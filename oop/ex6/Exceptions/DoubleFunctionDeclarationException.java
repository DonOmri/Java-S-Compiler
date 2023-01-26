package oop.ex6.Exceptions;

import oop.ex6.Exceptions.VariableExceptions.DoubleVariableDeclarationException;

public class DoubleFunctionDeclarationException extends Exception{

    private static final String DOUBLE_DECLARATION_MSG = "A function with that name was already declared." +
            "\nFunction name: ";

    public DoubleFunctionDeclarationException(String name){
        super(DOUBLE_DECLARATION_MSG + name);
    }
}
