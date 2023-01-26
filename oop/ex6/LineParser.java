package oop.ex6;

import java.util.regex.Matcher;
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
    private static final String EMPTY_LINE = "\\s*";
    private static final String COMMENT = "^//.*";
    private static final String RETURN = "\\s*return\\s*;\\s*";
    private static final String END_LINE = ";\\s*";

    /**
     * variableLineRegex
     **/
    private static final String FINAL = "\\s*(?:final)?\\s*"; ///todo should be + ??
    private static final String VARIABLE_TYPE = "\\s*(?:int|double|char|String|boolean)\\s+";
    private static final String VARIABLE_NAME = "\\s*(?:_\\w+|[a-zA-Z]\\w*)\\s*";
    private static final String VARIABLE_VALUE = "\\s*\\S+\\s*";
    private static final String EXTRA_VARIABLES = "\\s*(?:=" + VARIABLE_VALUE + "|," + VARIABLE_NAME +
            ")*\\s*";
    private static final String FINAL_AND_TYPE = "(" + FINAL + ")" + "(" + VARIABLE_TYPE + ")";

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
    private static final String WHILE_CONDITIONS = WHILE_CONDITION + "(" + AND_OR_CONDITION +
            WHILE_CONDITION + ")*";
    private static final String WHILE_END = IF_END;

    /**
     * functionDeclarationLineRegex
     **/
    private static final String FUNCTION_DECLARATION_START =
            "^\\s*void\\s+([a-zA-Z]([a-zA-Z\\d_])*)\\s*\\(\\s*";
    private static final String FUNCTION_DECLARATION_PARAMETER = FINAL + VARIABLE_TYPE + VARIABLE_NAME;
    private static final String FUNCTION_DECLARATION_PARAMETERS = "(" + FUNCTION_DECLARATION_PARAMETER +
            "(\\s*,\\s*" + FUNCTION_DECLARATION_PARAMETER + "\\s*)*)?";
    private static final String FUNCTION_DECLARATION_END = "\\)\\s*\\{\\s*$";
    private static final String FUNCTION_DECLARATION = FUNCTION_DECLARATION_START +
            FUNCTION_DECLARATION_PARAMETERS + FUNCTION_DECLARATION_END;

    /**
     * functionCallLineRegex
     **/
    private static final String FUNCTION_NAME = "\\s*([a-zA-Z][a-zA-Z\\d_]*)\\s*";
    private static final String FUNCTION_START = FUNCTION_NAME + "\\s*\\(\\s*";
    private static final String FUNCTION_PARAMETER = "\\s*(\\S+|\".*\"|\'.\')\\s*";
    private static final String FUNCTION_PARAMETERS = "(" + FUNCTION_PARAMETER + "(\\s*,\\s*" +
            FUNCTION_PARAMETER + "\\s*)*)?";
    private static final String FUNCTION_END = "\\s*\\)\\s*;\\s*";

    /**
     * Variable Values Regex
     */
    private static final String INT_REGEX = "(\\-|\\+)?\\d+";
    private static final String DOUBLE_REGEX = "((\\-|\\+)?\\d+.?\\d*?)|((\\-|\\+)?\\d*.?\\d+?)";
    private static final String BOOLEAN_REGEX = "true|false|" + INT_REGEX + DOUBLE_REGEX;
    private static final String STRING_REGEX = "\".*\"";
    private static final String CHAR_REGEX = "\'[\\S ]\'";
    private static final String POSSIBLE_ASSIGNMENT = "(" + VARIABLE_NAME +"=\\s*(" + VARIABLE_VALUE + "|" +
         VARIABLE_NAME + "),)*" + VARIABLE_NAME +"=\\s*(" + VARIABLE_VALUE + "|" + VARIABLE_NAME + ");\\s*";

    /**
     * Patterns
     **/
    private final Pattern ifLine;
    private final Pattern whileLine;
    private final Pattern functionLine;
    private final Pattern variableLine;
    private final Pattern commentLine;
    private final Pattern emptyLine;
    private final Pattern endOfScopeLine;
    private final Pattern returnLine;
    private final Pattern functionCallLine;
    private final Pattern possibleAssignmentLine;
    private final Pattern variablesPattern;
    private final Pattern finalAndTypePattern;
    private final Pattern charValuePattern;
    private final Pattern stringValuePattern;
    private final Pattern booleanValuePattern;
    private final Pattern doubleValuePattern;
    private final Pattern intValuePattern;

    /**
     * Constructor
     */
    public LineParser() {
        ifLine = Pattern.compile(IF_START + IF_CONDITIONS + IF_END);
        whileLine = Pattern.compile(WHILE_START + WHILE_CONDITIONS + WHILE_END);
        functionLine = Pattern.compile(FUNCTION_DECLARATION);
        variableLine = Pattern.compile(FINAL + VARIABLE_TYPE + VARIABLE_NAME + EXTRA_VARIABLES +
                END_LINE);
        emptyLine = Pattern.compile(EMPTY_LINE);
        commentLine = Pattern.compile(COMMENT);
        endOfScopeLine = Pattern.compile(END_OF_SCOPE);
        returnLine = Pattern.compile(RETURN);
        functionCallLine = Pattern.compile(FUNCTION_START + FUNCTION_PARAMETERS + FUNCTION_END);
        possibleAssignmentLine = Pattern.compile(POSSIBLE_ASSIGNMENT);

        variablesPattern = Pattern.compile(VARIABLE_NAME + "(?:=\\s*" + VARIABLE_VALUE +
                ")?\\s*(?:,|;)");
        finalAndTypePattern = Pattern.compile(FINAL_AND_TYPE);
        booleanValuePattern = Pattern.compile(BOOLEAN_REGEX);
        doubleValuePattern = Pattern.compile(DOUBLE_REGEX);
        intValuePattern = Pattern.compile(INT_REGEX);
        stringValuePattern = Pattern.compile(STRING_REGEX);
        charValuePattern = Pattern.compile(CHAR_REGEX);

    }


    /**
     * Returns the type of given line, based on parser regex
     *
     * @param line the given line
     * @return LineType which is the type of the line
     */
    public LineType parseLineType(String line) {
        if (functionLine.matcher(line).matches()) {
            return LineType.FUNCTION;
        } else if (commentLine.matcher(line).matches()) {
            return LineType.COMMENT;
        } else if (emptyLine.matcher(line).matches()) {
            return LineType.EMPTY;
        } else if (ifLine.matcher(line).matches()) {
            return LineType.IF;
        } else if (whileLine.matcher(line).matches()) {
            return LineType.WHILE;
        } else if (variableLine.matcher(line).matches()) {
            return LineType.VARIABLE;
        } else if (functionCallLine.matcher(line).matches()) {
            return LineType.FUNCTION_CALL;
        } else if (returnLine.matcher(line).matches()) {
            return LineType.RETURN;
        } else if (endOfScopeLine.matcher(line).matches()) {
            return LineType.END_OF_SCOPE;
        } else if (possibleAssignmentLine.matcher(line).matches()) {
            return LineType.POSSIBLE_ASSIGNMENT;
        } else {
            return LineType.UNRECOGNIZED;
        }
    }


    public String getFunctionName(String line){
        assert(functionLine.matcher(line).matches());
        Matcher matcher = Pattern.compile("([a-zA-Z0-9_]+)\\s*\\(").matcher(line);
        if(!matcher.find()){
            assert(false);
        }
        return matcher.group(1);
    }

    /**
     * Getters for patterns
     */
    public Pattern getFinalAndTypePattern() {
        return finalAndTypePattern;
    }
    public Pattern getVariablesPattern() {
        return variablesPattern;
    }
    public Pattern getBooleanValuesPattern() {return booleanValuePattern;}
    public Pattern getDoubleValuesPattern() {return doubleValuePattern;}
    public Pattern getIntValuesPattern() {return intValuePattern;}
    public Pattern getStringValuesPattern() {return stringValuePattern;}
    public Pattern getCharValuesPattern() {return charValuePattern;}
}
