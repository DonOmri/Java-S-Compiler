package oop.ex6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class classifies a given to line to a specific type, to later be verified by the verifier class
 */
public class LineParser {
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
    private static final String FINAL = "\\s*(?:final\\s)?\\s*";
    private static final String VARIABLE_TYPE = "\\s*(?:int|double|boolean|String|char)\\s+";
    private static final String VARIABLE_NAME = "\\s*(?:_\\w+|[a-zA-Z]\\w*)\\s*";
    private static final String FINAL_AND_TYPE = "(" + FINAL + ")" + "(" + VARIABLE_TYPE + ")";
    private static final String INT_REGEX = "([-+])?\\d+";
    private static final String DOUBLE_REGEX = "(([-+])?\\d+\\.?\\d*?)|(([-+])?\\d*\\.?\\d+?)";
    private static final String BOOLEAN_REGEX = "true|false|" + INT_REGEX + DOUBLE_REGEX;
    private static final String STRING_REGEX = "\"[\\S ]*\"";
    private static final String CHAR_REGEX = "\'[\\S ]?\'";
//    private static final String GENERAL_VARIABLE_VALUE = VARIABLE_NAME + "\\s*|\\s*" + INT_REGEX +
//            "\\s*|\\s*" + DOUBLE_REGEX + "\\s*|\\s*" + BOOLEAN_REGEX + "\\s*|\\s*" + STRING_REGEX +
//            "\\s*|\\s*" + CHAR_REGEX  + "\\s*";

    private static final String GENERAL_VARIABLE_VALUE = "\\s*(?:(\\S)+|\"[\\S ]*\"|\'[\\S ]\')\\s*";
    private static final String EXTRA_VARIABLES = "\\s*(?:=" + GENERAL_VARIABLE_VALUE + "|," +
            VARIABLE_NAME + ")*\\s*";
    private static final String POSSIBLE_ASSIGNMENT = VARIABLE_NAME +"=\\s*(" + GENERAL_VARIABLE_VALUE + "|" +
            VARIABLE_NAME + ")";
    private static final String POSSIBLE_ASSIGNMENT_LINE = "(?:" + POSSIBLE_ASSIGNMENT + ",)*" +
            POSSIBLE_ASSIGNMENT + ";\\s*";

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
     * functionCallLineRegex
     **/
    private static final String FUNCTION_NAME = "\\s*([a-zA-Z]\\w*)\\s*";
    private static final String FUNCTION_START = FUNCTION_NAME + "\\s*\\(\\s*";
    private static final String FUNCTION_PARAMETER = "\\s*(\\S+|\".*\"|\'.\')\\s*";
    private static final String FUNCTION_PARAMETERS = "(" + FUNCTION_PARAMETER + "(\\s*,\\s*" +
            FUNCTION_PARAMETER + "\\s*)*)?";
    private static final String FUNCTION_END = "\\s*\\)\\s*;\\s*";

    String x = "\\s*([a-zA-Z]\\w*)\\s*" + "\\s*\\(\\s*";
    String y = "x";

    /**
     * functionDeclarationLineRegex
     **/
    private static final String FUNCTION_DECLARATION_START = "^\\s*void\\s+" + FUNCTION_NAME + "\\(";
    private static final String FUNCTION_DECLARATION_PARAMETER = FINAL + VARIABLE_TYPE + VARIABLE_NAME;
    private static final String FUNCTION_DECLARATION_PARAMETERS =
            "(?:" + FUNCTION_DECLARATION_PARAMETER + "(?:," + FUNCTION_DECLARATION_PARAMETER + ")*)?";
    private static final String FUNCTION_DECLARATION_END = "\\)\\s*\\{\\s*";
    private static final String FUNCTION_LINE_FOR_VARIABLES = "\\w+\\s+\\w+\\s*\\(([^)]*)\\)";


    /**
     * Patterns
     **/
    private final Pattern ifLinePattern;
    private final Pattern whileLinePattern;
    private final Pattern functionLinePattern;
    private final Pattern variableLinePattern;
    private final Pattern commentLinePattern;
    private final Pattern emptyLinePattern;
    private final Pattern endOfScopeLinePattern;
    private final Pattern returnLinePattern;
    private final Pattern functionCallLinePattern;
    private final Pattern possibleAssignmentLinePattern;
    private final Pattern variablesPattern;
    private final Pattern finalAndTypePattern;
    private final Pattern charValuePattern;
    private final Pattern stringValuePattern;
    private final Pattern booleanValuePattern;
    private final Pattern doubleValuePattern;
    private final Pattern intValuePattern;
    private final Pattern functionLineForVariables;
    private static final Pattern variableTypePattern = Pattern.compile(VARIABLE_NAME);

    /**
     * Constructor
     */
    public LineParser() {
        ifLinePattern = Pattern.compile(IF_START + IF_CONDITIONS + IF_END);
        whileLinePattern = Pattern.compile(WHILE_START + WHILE_CONDITIONS + WHILE_END);
        functionLinePattern = Pattern.compile(FUNCTION_DECLARATION_START +
                FUNCTION_DECLARATION_PARAMETERS + FUNCTION_DECLARATION_END);
        variableLinePattern = Pattern.compile(FINAL + VARIABLE_TYPE + VARIABLE_NAME + EXTRA_VARIABLES +
                END_LINE);
        emptyLinePattern = Pattern.compile(EMPTY_LINE);
        commentLinePattern = Pattern.compile(COMMENT);
        endOfScopeLinePattern = Pattern.compile(END_OF_SCOPE);
        returnLinePattern = Pattern.compile(RETURN);
        functionCallLinePattern = Pattern.compile(FUNCTION_START + FUNCTION_PARAMETERS + FUNCTION_END);
        possibleAssignmentLinePattern = Pattern.compile(POSSIBLE_ASSIGNMENT_LINE);
        variablesPattern = Pattern.compile(VARIABLE_NAME + "(?:=\\s*" + GENERAL_VARIABLE_VALUE + ")?\\s*[,;]");
        finalAndTypePattern = Pattern.compile(FINAL_AND_TYPE);
        booleanValuePattern = Pattern.compile(BOOLEAN_REGEX);
        doubleValuePattern = Pattern.compile(DOUBLE_REGEX);
        intValuePattern = Pattern.compile(INT_REGEX);
        stringValuePattern = Pattern.compile(STRING_REGEX);
        charValuePattern = Pattern.compile(CHAR_REGEX);
        functionLineForVariables = Pattern.compile(FUNCTION_LINE_FOR_VARIABLES);
    }

    /**
     * Returns the type of given line, based on parser regex
     * @param line the given line
     * @return LineType which is the type of the line
     */
    public LineType parseLineType(String line) {
        if (functionLinePattern.matcher(line).matches()) return LineType.FUNCTION;
            else if (commentLinePattern.matcher(line).matches()) return LineType.COMMENT;
            else if (emptyLinePattern.matcher(line).matches()) return LineType.EMPTY;
            else if (ifLinePattern.matcher(line).matches()) return LineType.IF;
            else if (whileLinePattern.matcher(line).matches()) return LineType.WHILE;
            else if (variableLinePattern.matcher(line).matches()) return LineType.VARIABLE;
            else if (functionCallLinePattern.matcher(line).matches()) return LineType.FUNCTION_CALL;
            else if (returnLinePattern.matcher(line).matches()) return LineType.RETURN;
            else if (endOfScopeLinePattern.matcher(line).matches()) return LineType.END_OF_SCOPE;
            else if (possibleAssignmentLinePattern.matcher(line).matches()) return LineType.POSSIBLE_ASSIGN;
            else return LineType.UNRECOGNIZED;
    }


    /**
     * Gets function name from a given line
     * @param line the line itself
     * @return the function name
     */
    public String getFunctionName(String line){
        assert(functionLinePattern.matcher(line).matches());
        Matcher matcher = Pattern.compile("([a-zA-Z0-9_]+)\\s*\\(").matcher(line);
        if(!matcher.find()) assert(false);
        return matcher.group(1);
    }

    /**
     * Getters for patterns
     */
    public Pattern getFinalAndTypePattern() {return finalAndTypePattern;}
    public Pattern getVariablesPattern() {return variablesPattern;}
    public Pattern getBooleanValuesPattern() {return booleanValuePattern;}
    public Pattern getDoubleValuesPattern() {return doubleValuePattern;}
    public Pattern getIntValuesPattern() {return intValuePattern;}
    public Pattern getStringValuesPattern() {return stringValuePattern;}
    public Pattern getCharValuesPattern() {return charValuePattern;}
    public Pattern getFunctionLinePattern() {return functionLineForVariables;}
    public Pattern getFunctionCallLinePattern() {return functionCallLinePattern;}
    public static Pattern getVariableTypePattern() {return variableTypePattern;}
    public static String[] getVariableRegexes(){
        return new String[]{INT_REGEX, DOUBLE_REGEX, BOOLEAN_REGEX, STRING_REGEX, CHAR_REGEX};
    }
}