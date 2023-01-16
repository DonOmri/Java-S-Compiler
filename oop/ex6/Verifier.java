package oop.ex6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verifier {

    private static final int GLOBAL_SCOPE_INDEX = 0;  //reach the global scope

    //holds all relevant data for each scope, including the global scope (at index 0)
    private final List<Scope> scopes = new ArrayList<>();

    //holds the function names and line number
    private final HashMap<String, Integer> functions = new HashMap<>();

    private static final String IGNORED_LINE_REGEX = "[ \t]*//.*|\\s*";
    private static final String VARIABLE_TYPE_REGEX = "[ \t]*(?:int|double|char|String|boolean)[ \t]*";
    private static final String FINAL_REGEX = "[ \t]*(?:final)?[ \t]*";
    public static final String SEPARATOR_REGEX = "[ \t]+";
    private static final String NAME_REGEX = "(?:_\\w+|[a-zA-Z]\\w*)";
    private static final String METHOD_NAME_REGEX = "[ \t]*void(?:[ \t])+[a-zA-Z]+\\w*";
    private static Matcher matcher = null;

    public Verifier() {

    }

    public void verifySjavacFile(String[] args) throws Exception {

        FileReader fileReader = new FileReader(args[0]);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        //1st pass - verify the global scope
        verifyGlobalScope(bufferedReader);

        //2nd pass - verify the inside of the functions
        verifyFunctions(bufferedReader);
    }

    //todo pattern,compile is relatively expensive. create a separate class for all patterns
    private void verifyGlobalScope(BufferedReader bufferedReader) throws IOException {

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (Pattern.compile(IGNORED_LINE_REGEX).matcher(line).matches()) continue;
            else if (Pattern.compile(FINAL_REGEX + VARIABLE_TYPE_REGEX + NAME_REGEX).matcher(line).matches())
                parseVariableLine(line);
            else if (Pattern.compile(METHOD_NAME_REGEX).matcher(line).matches()) parseFunctionLine(line);
            else System.out.println("bad line"); //should throw exception
        }
    }


    private void verifyFunctions(BufferedReader bufferedReader) throws Exception {
        //iterate over functions that were saved in the 1st pass
        //for each function, add a new scope to the ArrayList, and verify it.

    }

    private void parseFunctionLine(String line) throws IOException{
        System.out.println("FUNCTION PARSE");
    }

    private void parseVariableLine(String line) throws IOException{
        List<String> words = Arrays.asList(line.split(SEPARATOR_REGEX));
        words.removeIf(word -> word.equals("\t"));
        boolean isFinal = words.get(0).equals("final");
        for (var word : words) System.out.println("word: " + word);
    }
}
