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

    private static final String IGNORED_LINE_REGEX = "[ \\t]//.|\\s*";
    private static final String VARIABLE_TYPE_REGEX = "[ \\t](?:int|double|char|String|boolean)[ \\t]";
    private static final String FINAL_REGEX = "[ \\t](?:final)?[ \\t]";
    public static final String SEPARATOR_REGEX = "[ \t]+";
    private static final String NAME_REGEX = "(?:_\\w+|[a-zA-Z]\\w*)";
    private static final String METHOD_NAME_REGEX = "[ \\t]void(?:[ \\t])+[a-zA-Z]+\\w";
    private static Matcher matcher = null;

    public Verifier() {

    }

    public void verifySjavacFile(String[] args) throws Exception {

        FileReader fileReader = new FileReader(args[0]);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        scopes.add(new Scope()); //global scope

        //1st pass - verify the global scope
        verifyGlobalScope(bufferedReader);
        System.out.println("DONE");
        for (var variable : scopes.get(0).variablesMap.entrySet()) System.out.println(variable);

        //2nd pass - verify the inside of the functions
        verifyFunctions(bufferedReader);
    }

    //todo pattern,compile is relatively expensive. create a separate class for all patterns

    /**
     * Classify each line as either comment/empty line, variable line, function line or bad line. then calls
     * to corresponding function to verify the line itself is legal
     * @param bufferedReader
     * @throws IOException
     */
    private void verifyGlobalScope(BufferedReader bufferedReader) throws IOException {

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (Pattern.compile(IGNORED_LINE_REGEX).matcher(line).matches()) continue;
            else if (Pattern.compile(FINAL_REGEX + VARIABLE_TYPE_REGEX + NAME_REGEX).matcher(line).find())
                parseVariableLine(line);
            else if (Pattern.compile(METHOD_NAME_REGEX).matcher(line).matches()) parseFunctionLine(line);
            else System.out.println("bad line: " + line); //should throw exception
        }
    }

    //TODO: update to throw whatever we want

    /**
     * This function is called when verification has failed due to syntax or logical problems.
     * It throws a proper exception.
     * @throws Exception regarding the issue verification failed
     */
    public void verificationFailed() throws Exception{
        throw new Exception();
    }

    /**
     * This function validates the structure of a function opening line.
     * @param line the given line, unprocessed
     * @return true if the structure of the line is valid, false otherwise.
     */
    private boolean validFunctionOpeningLine(String line){
        String until_params_regex = "^\\s*void\\s+([a-zA-Z][a-zA-Z\\d_])\\s\\(";
        String final_op =  "[ \t](?:final +)?[ \t]";
        String type = "(int|char|String|boolean|double)\\s+";
        String name = "(?:_\\w+|[a-zA-Z]\\w*)[ \t]*";
        String param = final_op + type + name;
        String params = param + "([ \t],[ \t]" + param + "[ \t])";
        String after_params_regex = "\\)\\s*\\{\\s*$";
        String regex = until_params_regex + params + after_params_regex;
        return Pattern.compile(regex).matcher(line).matches();
    }

    /**
     * This function validates the structure of an if opening line.
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private static boolean validIfLine(String line){
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
     * This function validates the structure of a while opening line.
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private boolean validWhileLine(String line){
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
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private boolean validVariableLine(String line){
        return Pattern.compile(FINAL_REGEX + VARIABLE_TYPE_REGEX + NAME_REGEX).matcher(line).matches();
    }

    /**
     * This function validates the structure of a function call line.
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private boolean validFunctionCall(String line){
        String funcName = "([a-zA-Z][a-zA-Z\\d_]*)";
        String start = "^\\s*" + funcName + "\\s*" + "\\(\\s*";
        String param = "\\s*\\S*\\s*";
        String params = "(" + param + "([ \t],[ \t]" + param + "[ \t]))?";
        String end = "\\s*\\)\\s*;\\s*";
        return Pattern.compile(start + params + end).matcher(line).matches();
    }

    /**
     * This function validates the structure of a return statement.
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private boolean validReturnLine(String line){
        String regex = "\\s*return\\s*;\\s*";
        return Pattern.compile(regex).matcher(line).matches();
    }

    /**
     * This function validates the structure of a } symbol that stands for end of current scope.
     * @param line the given line, unprocessed
     * @return true if the structure is valid, false otherwise.
     */
    private boolean validEndOfScope(String line){
        String regex = "\\s*}\\s*";
        return Pattern.compile(regex).matcher(line).matches();
    }

    /**
     * This function iterates through all the functions found in the 1st pass, one by one, and for each function
     * validates all the internal lines of the function.
     * @param bufferedReader the reader
     * @throws Exception throws a proper agreed upon exception if validation fails for some reason.
     */
    private void verifyFunctions(BufferedReader bufferedReader) throws Exception {
        //iterate over functions that were saved in the 1st pass
        for (HashMap.Entry<String, Integer> entry : functions.entrySet()) {
            String funcName = entry.getKey();
            Integer startLine = entry.getValue();

            //iterate from start of the function
            for (int i = 0; i < startLine; ++i)
                bufferedReader.readLine();
            String line;
            int line_num = 0;
            int last_return_line_num = -1;
            int numScopes = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if(validFunctionOpeningLine(line)){
                    //add a new scope to the linked list
                    scopes.add(new Scope());
                    numScopes++;

                    //add parameters as local variables in the scope
                    //init variables - final or not, with type and name
                }
                else if(validIfLine(line)){
                    // if - opens a new scope
                    scopes.add(new Scope());
                    numScopes++;
                }
                else if(validWhileLine(line)){
                    // while - opens a new scope
                    scopes.add(new Scope());
                    numScopes++;
                }
                else if(validVariableLine(line)){
                    // variable - parsed just like global, but added to the last scope in the linked list
                    // for assignment, check that the variable exists, and that the assignment succeeds
                }
                else if(validFunctionCall(line)){
                    // function call - method name exists, params should be - good types, initialized, same number
                }
                else if (validReturnLine(line)) {
                    // return
                    last_return_line_num = line_num;
                }
                else if (validEndOfScope(line)){
                    //delete the last scope from the linked list of scopes
                    scopes.remove(scopes.size()-1);
                    --numScopes;

                    //check if this was the closing } of the function scope
                    if(numScopes == 0){
                        //verify that last line was a valid 'return;' statement
                        if(last_return_line_num != line_num - 1){
                            // verification failed
                            verificationFailed();
                        }
                        //if at this point no exception was thrown, we break out of the loop and the function is verified.
                        break;
                    }
                }
                line_num++;
            }
        }
    }

    private void parseFunctionLine(String line) throws IOException{
        System.out.println("FUNCTION PARSE");
    }

    private void parseVariableLine(String line) throws IOException{
        List<String> words = Arrays.asList(line.replaceFirst("^\\s+", "").split("\\s+"));

        Variable newVariable = words.get(0).equals("final") ?
                new Variable(words.get(1),words.get(2), true) :
                new Variable(words.get(0),words.get(1), false);

        if (words.get(0).equals("final")){ //check if final is assigned

            if (words.size() != 5 || !words.get(3).equals("=")) System.out.println("wtf " + line);;
        }

        scopes.get(scopes.size() - 1).addVariable(newVariable);
    }

    private boolean isTypeMatchesValue(Variable variable){
        return true;
    }
}