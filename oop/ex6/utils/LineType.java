package oop.ex6.utils;

/**
 * This enum classifies the possible line types the sJavac can parse
 */
public enum LineType {
    COMMENT,
    VARIABLE,
    FUNCTION,
    IF_WHILE,
    FUNCTION_CALL,
    RETURN,
    END_OF_SCOPE,
    EMPTY,
    POSSIBLE_ASSIGN,
    UNRECOGNIZED
}