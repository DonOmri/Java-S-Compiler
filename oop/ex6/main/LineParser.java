package oop.ex6.main;

import oop.ex6.utils.LineType;

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
    private static final String FINAL = "\\s*(?:final\\s+)?\\s*";
    private static final String VARIABLE_TYPE = "\\s*(?:int|double|boolean|String|char)\\s+";
    private static final String VARIABLE_NAME = "\\s*(?:_\\w+|[a-zA-Z]\\w*)\\s*";
    private static final String FINAL_AND_TYPE = "(" + FINAL + ")(" + VARIABLE_TYPE + ")";
    private static final String INT_VALUE = "\\s*[-+]?\\d+\\s*";
    private static final String DOUBLE_VALUE = "\\s*[-+]?((\\d+\\.?\\d*)|(\\.\\d+))\\s*";
    private static final String BOOLEAN_VALUE = "\\s*((true[^;,\\w]*)|(false[^;,\\w]*))|(" + DOUBLE_VALUE +
            ")\\s*";
    private static final String NON_BOOLEAN_VALUE = "\\s*(?:(\\s*true\\s*[^\";,\\s]+)|(\\s*false\\s*" +
            "[^\";,\\s]+))\\s*|\\s*\"(?:(\\s*true\\s*[^\";,\\s]+)|(\\s*false\\s*[^\";,\\s]+))\\s*\"";
    private static final String STRING_VALUE = "\\s*\"[^\"'\\\\,]*\"\\s*";
    private static final String CHAR_VALUE = "\\s*'[\\S ]?'\\s*";
    private static final String GENERAL_VARIABLE_VALUE = "((" + STRING_VALUE + ")|(" + CHAR_VALUE +
            ")|(" + BOOLEAN_VALUE + ")|(" + VARIABLE_NAME + "))";
    private static final String EXTRA_VARIABLES = "\\s*(?:=" + GENERAL_VARIABLE_VALUE + "|," +
            VARIABLE_NAME + ")*\\s*";
    private static final String POSSIBLE_ASSIGNMENT = VARIABLE_NAME +"(=\\s*(?:" + GENERAL_VARIABLE_VALUE +
            "|" + VARIABLE_NAME + "))?";
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
    private static final String FUNCTION_PARAMETER = "\\s*(\\S+|\".*\"|'.')\\s*";
    private static final String FUNCTION_PARAMETERS = "(" + FUNCTION_PARAMETER + "(\\s*,\\s*" +
            FUNCTION_PARAMETER + "\\s*)*)?";
    private static final String FUNCTION_END = "\\s*\\)\\s*;\\s*";

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
    private final Pattern nonBooleanValue;
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
        variablesPattern = Pattern.compile(POSSIBLE_ASSIGNMENT);
        finalAndTypePattern = Pattern.compile(FINAL_AND_TYPE);
        booleanValuePattern = Pattern.compile(BOOLEAN_VALUE);
        doubleValuePattern = Pattern.compile(DOUBLE_VALUE);
        intValuePattern = Pattern.compile(INT_VALUE);
        stringValuePattern = Pattern.compile(STRING_VALUE);
        charValuePattern = Pattern.compile(CHAR_VALUE);
        functionLineForVariables = Pattern.compile(FUNCTION_LINE_FOR_VARIABLES);
        nonBooleanValue = Pattern.compile(NON_BOOLEAN_VALUE);
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
            else if (variableLinePattern.matcher(line).matches() && !nonBooleanValue.matcher(line).find())
                return LineType.VARIABLE;
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
    public static String[] getVariableRegexes(){
        return new String[]{INT_VALUE, DOUBLE_VALUE, BOOLEAN_VALUE, STRING_VALUE, CHAR_VALUE};
    }
    public static Pattern getVariableTypePattern() {return variableTypePattern;}
}