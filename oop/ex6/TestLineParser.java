package oop.ex6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class TestLineParser {

    public static final String comments = "comments";
    public static final String end_of_scope = "end_scope";
    public static final String function = "function";
    public static final String function_call = "function_call";
    public static final String return_stmt = "return";
    public static final String if_stmt = "if";
    public static final String while_stmt = "while";
    public static final String variable = "variable";
    public static final String[] checks = {comments, end_of_scope, function, function_call, return_stmt, if_stmt, while_stmt, variable};
    public static final HashMap<String, LineType> map = new HashMap<>(Map.ofEntries(
            entry(comments, LineType.COMMENT),
            entry(end_of_scope, LineType.END_OF_SCOPE),
            entry(function, LineType.FUNCTION),
            entry(function_call, LineType.FUNCTION_CALL),
            entry(return_stmt, LineType.RETURN),
            entry(if_stmt, LineType.IF),
            entry(while_stmt, LineType.WHILE),
            entry(variable, LineType.VARIABLE)
    ));


    public static float test_invalid(String file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        LineParser parser = new LineParser();
        String line;
        int line_number = 0, failures = 0;
        while ((line = bufferedReader.readLine()) != null) {
            LineType type = parser.parseLineType(line);
            if (type != LineType.UNRECOGNIZED) {
                System.out.println("failed on line " + line_number + ". Thought it's " + type);
                failures++;
            }
            line_number++;
        }
        if(line_number == 0) return 0;
        return (failures / (float)line_number);
    }

    public static float test_valid(String file, LineType desired) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        LineParser parser = new LineParser();
        String line;
        int line_number = 0, failures = 0;
        while ((line = bufferedReader.readLine()) != null) {
            LineType type = parser.parseLineType(line);
            if (type != desired) {
                System.out.println("failed on line " + line_number + ". Thought it's " + type +
                        " but it's supposed to be " + desired);
                failures++;
            }
            line_number++;
        }
        if(line_number == 0) return 0;
        return (failures / (float)line_number);
    }

    public static void main(String[] args) throws IOException {
        String invalid_path = "line_parser_tests/invalid/";
        String valid_path = "line_parser_tests/valid/";

        float invalid_failure_rate, valid_failure_rate;
        for (String check : checks) {

            String file = invalid_path + check + ".txt";
            invalid_failure_rate = test_invalid(file);
            if(invalid_failure_rate > 0)
                System.out.println(" % of failed checks for " + file + ": " + invalid_failure_rate * 100 + "%");

            file = valid_path + check + ".txt";
            valid_failure_rate = test_valid(file, map.get(check));
            if(valid_failure_rate > 0)
                System.out.println(" % of failed checks for " + file + ": " + valid_failure_rate * 100 + "%");
        }
        System.out.println("\n------- ALL OTHER TEST CASES PASSED --------");
    }

}
