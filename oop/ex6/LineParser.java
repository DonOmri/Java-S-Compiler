package oop.ex6;

import java.util.regex.Pattern;

/**
 * This class classifies a given to line to a specific type, to later be verified by the verifier class
 */
public class LineParser {
    //todo: use possessives (video 4)
    //todo: increase efficiency by using ^ to search in the start of the string (relevant for any line type)

    /**
     * generalRegex
     **/
    private static final String END_OF_SCOPE = "\\s*}\\s*";
    private static final String IGNORED_LINE = "\\s*//.*|\\s*";
    private static final String RETURN = "\\s*return\\s*;\\s*";
    private static final String END_LINE = ";\\s*";

    /**
     * variableLineRegex
     **/
    private static final String FINAL = "\\s*(?:final)?\\s*"; ///todo should be + ??
    private static final String VARIABLE_TYPE = "\\s*(?:int|double|char|String|boolean)\\s+";
    private static final String VARIABLE_NAME = "\\s*(?:_\\w+|[a-zA-Z]\\w*)\\s*";
    private static final String VARIABLE_VALUE = "\\s*\\S+\\s*";
    private static final String EXTRA_VARIABLES = "\\s*(?:=" + VARIABLE_VALUE + "|," + VARIABLE_NAME + ")*\\s*";

    /**
     * ifLineRegex
     **/
    private static final String IF_START = "^\\s*if\\s*\\(\\s*";
    private static final String NUMBER = "-?\\d+(.\\d+)?";
    private static final String IF_CONDITION = "\\s*(" + VARIABLE_NAME + "|true|false|" + NUMBER + ")\\s*";
    private static final String AND_OR_CONDITION = "\\s*(\\|\\||&&)\\s*";
    private static final String IF_CONDITIONS = IF_CONDITION + "(" + AND_OR_CONDITION + IF_CONDITION + ")*";
    private static final String IF_END = "\\s*\\)\\s*\\{\\s*";

    /**
     * whileLineRegex
     **/
    private static final String WHILE_START = "^\\s*while\\s*\\(\\s*";
    private static final String WHILE_CONDITION = IF_CONDITION;
    private static final String WHILE_CONDITIONS = WHILE_CONDITION + "(" + AND_OR_CONDITION + WHILE_CONDITION + ")*";
    private static final String WHILE_END = IF_END;

    /**
     * functionDeclarationLineRegex
     **/
    private static final String FUNCTION_DECLARATION_START = "^\\s*void\\s+([a-zA-Z]([a-zA-Z\\d_])*)\\s*\\(\\s*";
    private static final String FUNCTION_DECLARATION_PARAMETER = FINAL + VARIABLE_TYPE + VARIABLE_NAME;
    private static final String FUNCTION_DECLARATION_PARAMETERS = "(" + FUNCTION_DECLARATION_PARAMETER + "(\\s*,\\s*" + FUNCTION_DECLARATION_PARAMETER + "\\s*)*)?";
    private static final String FUNCTION_DECLARATION_END = "\\)\\s*\\{\\s*$";
    private static final String FUNCTION_DECLARATION = FUNCTION_DECLARATION_START + FUNCTION_DECLARATION_PARAMETERS + FUNCTION_DECLARATION_END;

    /**
     * functionCallLineRegex
     **/
    private static final String FUNCTION_NAME = "\\s*([a-zA-Z][a-zA-Z\\d_]*)\\s*";
    private static final String FUNCTION_START = FUNCTION_NAME + "\\s*\\(\\s*";
    private static final String FUNCTION_PARAMETER = "\\s*\\S+\\s*";
    private static final String FUNCTION_PARAMETERS = "(" + FUNCTION_PARAMETER + "(\\s*,\\s*" + FUNCTION_PARAMETER + "\\s*)*)?";
    private static final String FUNCTION_END = "\\s*\\)\\s*;\\s*";

    /**
     * Patterns
     **/
    private final Pattern ifLine;
    private final Pattern whileLine;
    private final Pattern functionLine;
    private final Pattern variableLine;
    private final Pattern ignoredLine;
    private final Pattern endOfScopeLine;
    private final Pattern returnLine;
    private final Pattern functionCallLine;

    private final Pattern variablesPattern;
    private final Pattern finalAndTypePattern;

    //    private static final String start = "^\\s*" + FUNCTION_NAME_REGEX + "\\s*" + "\\(\\s*";
//    private static final String METHOD_NAME_REGEX = "[ \\t]void(?:[ \\t])+[a-zA-Z]+\\w";

    /**
     * Constructor
     */
    public LineParser() {
        ifLine = Pattern.compile(IF_START + IF_CONDITIONS + IF_END);
        whileLine = Pattern.compile(WHILE_START + WHILE_CONDITIONS + WHILE_END);
        functionLine = Pattern.compile(FUNCTION_DECLARATION);
        variableLine = Pattern.compile(FINAL + VARIABLE_TYPE + VARIABLE_NAME + EXTRA_VARIABLES + END_LINE);
        ignoredLine = Pattern.compile(IGNORED_LINE);
        endOfScopeLine = Pattern.compile(END_OF_SCOPE);
        returnLine = Pattern.compile(RETURN);
        functionCallLine = Pattern.compile(FUNCTION_START + FUNCTION_PARAMETERS + FUNCTION_END);


        variablesPattern = Pattern.compile(VARIABLE_NAME + EXTRA_VARIABLES);
        finalAndTypePattern = Pattern.compile("(" + FINAL + ")" + "(" + VARIABLE_TYPE + ")");
    }


    /**
     * All following functions validate a certain type of line (STRUCTURE ONLY)
     *
     * @param line the given line, unprocessed
     * @return true if the structure of the line is valid, false otherwise.
     */
    private boolean validIgnoredLine(String line) {
        return ignoredLine.matcher(line).matches();
    }

    private boolean validVariableLine(String line) {
        return variableLine.matcher(line).matches();
    }

    private boolean validIfLine(String line) {
        return ifLine.matcher(line).matches();
    }

    private boolean validWhileLine(String line) {
        return whileLine.matcher(line).matches();
    }

    private boolean validFunctionDeclarationLine(String line) {
        return functionLine.matcher(line).matches();
    }

    private boolean validFunctionCall(String line) {
        return functionCallLine.matcher(line).matches();
    }

    private boolean validReturnLine(String line) {
        return returnLine.matcher(line).matches();
    }

    private boolean validEndOfScope(String line) {
        return endOfScopeLine.matcher(line).matches();
    }

    /**
     * Returns type of line, based on the valid functions above
     *
     * @param line the given line
     * @return LineType which is the type of the line
     */
    public LineType parseLineType(String line) {
        if (validFunctionDeclarationLine(line)) {
            return LineType.FUNCTION;
        } else if (validIgnoredLine(line)) {
            return LineType.COMMENT;
        } else if (validIfLine(line)) {
            return LineType.IF;
        } else if (validWhileLine(line)) {
            return LineType.WHILE;
        } else if (validVariableLine(line)) {
            return LineType.VARIABLE;
        } else if (validFunctionCall(line)) {
            return LineType.FUNCTION_CALL;
        } else if (validReturnLine(line)) {
            return LineType.RETURN;
        } else if (validEndOfScope(line)) {
            return LineType.END_OF_SCOPE;
        } else {
            return LineType.UNRECOGNIZED;
        }
    }


    /**
     * WORKING ZONE
     **/

    public Pattern getFinalAndTypePattern() {
        return finalAndTypePattern;
    }

    public Pattern getVariablesPattern() {
        return variablesPattern;
    }

}
