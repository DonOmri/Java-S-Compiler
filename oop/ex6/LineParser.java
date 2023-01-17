package oop.ex6;

import java.util.regex.Pattern;

public class LineParser {

    public static final String SEPARATOR_REGEX = "[ \t]+";
    private static final String IGNORED_LINE_REGEX = "[ \\t]*//.*|\\s*";
    private static final String VARIABLE_TYPE_REGEX = "[ \\t](?:int|double|char|String|boolean)[ \\t]";
    private static final String FINAL_REGEX = "[ \\t](?:final)?[ \\t]";
    private static final String NAME_REGEX = "(?:_\\w+|[a-zA-Z]\\w*)";
    private static final String METHOD_NAME_REGEX = "[ \\t]void(?:[ \\t])+[a-zA-Z]+\\w";

    /**
     * This function validates the structure of an if opening line.
     *
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private static boolean validIfLine(String line) {
        //doesn't check what is the word in the condition, just that the structure is ok
        String start = "^\\s*if\\s*\\(\\s*";
        String name = "(?:_\\w+|[a-zA-Z]\\w*)[ \t]*";
        String number = "-?\\d+(.\\d+)?";
        String condition = "\\s*(" + name + "|true|false|" + number + ")\\s*";
        String separator = "\\s*(\\|\\|\\s*|\\s*&&)\\s*";
        String conditions = condition + "(" + separator + condition + ")*";
        String end = "\\s*\\)\\s*\\{\\s*";
        String regex = start + conditions + end;
        return Pattern.compile(regex).matcher(line).matches();
    }

    /**
     * This function validates the structure of a comment line.
     *
     * @param line the given line, unprocessed
     * @return true if the structure of the line is valid, false otherwise.
     */
    private boolean validCommentLine(String line) {
        return Pattern.compile(IGNORED_LINE_REGEX).matcher(line).matches();
    }

    /**
     * This function validates the structure of a function opening line.
     *
     * @param line the given line, unprocessed
     * @return true if the structure of the line is valid, false otherwise.
     */
    private boolean validFunctionOpeningLine(String line) {
//        return Pattern.compile(METHOD_NAME_REGEX).matcher(line).matches();


        String until_params_regex = "^\\s*void\\s+([a-zA-Z][a-zA-Z\\d_])\\s\\(";
        String final_op = "[ \t](?:final +)?[ \t]";
        String type = "(int|char|String|boolean|double)\\s+";
        String name = "(?:_\\w+|[a-zA-Z]\\w*)[ \t]*";
        String param = final_op + type + name;
        String params = param + "([ \t],[ \t]" + param + "[ \t])";
        String after_params_regex = "\\)\\s*\\{\\s*$";
        String regex = until_params_regex + params + after_params_regex;
        return Pattern.compile(regex).matcher(line).matches();
    }

    /**
     * This function validates the structure of a while opening line.
     *
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private boolean validWhileLine(String line) {
        //doesn't check what is the word in the condition, just that the structure is ok
        String start = "^\\s*while\\s*\\(\\s*";
        String name = "(?:_\\w+|[a-zA-Z]\\w*)[ \t]*";
        String number = "-?\\d+(.\\d+)?";
        String condition = "\\s*(" + name + "|true|false|" + number + ")\\s*";
        String separator = "\\s*(\\|\\|\\s*|\\s*&&)\\s*";
        String conditions = condition + "(" + separator + condition + ")*";
        String end = "\\s*\\)\\s*\\{\\s*";
        String regex = start + conditions + end;
        return Pattern.compile(regex).matcher(line).matches();
    }

    /**
     * This function validates the structure of a variable initialization/declaration line.
     *
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private boolean validVariableLine(String line) {
//        return Pattern.compile(FINAL_REGEX + VARIABLE_TYPE_REGEX + NAME_REGEX).matcher(line).find();
        return Pattern.compile(FINAL_REGEX + VARIABLE_TYPE_REGEX + NAME_REGEX).matcher(line).matches();
    }

    /**
     * This function validates the structure of a function call line.
     *
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private boolean validFunctionCall(String line) {
        String funcName = "([a-zA-Z][a-zA-Z\\d_]*)";
        String start = "^\\s*" + funcName + "\\s*" + "\\(\\s*";
        String param = "\\s*\\S*\\s*";
        String params = "(" + param + "([ \t],[ \t]" + param + "[ \t]))?";
        String end = "\\s*\\)\\s*;\\s*";
        return Pattern.compile(start + params + end).matcher(line).matches();
    }

    /**
     * This function validates the structure of a return statement.
     *
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private boolean validReturnLine(String line) {
        String regex = "\\s*return\\s*;\\s*";
        return Pattern.compile(regex).matcher(line).matches();
    }

    /**
     * This function validates the structure of a } symbol that stands for end of current scope.
     *
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private boolean validEndOfScope(String line) {
        String regex = "\\s*}\\s*";
        return Pattern.compile(regex).matcher(line).matches();
    }

    /**
     * This function returns the type of the line.
     *
     * @param line the given line
     * @return LineType which is the type of the line
     */
    public LineType getLineType(String line) {
        if (validFunctionOpeningLine(line)) {
            return LineType.FUNCTION;
        } else if (validCommentLine(line)) {
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

}
