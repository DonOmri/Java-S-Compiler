package oop.ex6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

public class Verifier {

    //holds all relevant data for each scope, including the global scope (at index 0)
    private final List<Scope> scopes = new ArrayList<>();
    //holds the function names and line number
    private final HashMap<String, Integer> functions = new HashMap<>();
    private BufferedReader bufferedReader;
    private LineParser parser;

    /**
     * This function receives a path to a sJavac file, and verifies its validity.
     * @param file a path to the sJavac file to check
     * @throws Exception in case of problems
     */
    public void verifySjavacFile(String file) throws Exception {
        this.bufferedReader = new BufferedReader(new FileReader(file)); //throw here exceptions, remember to close
        this.parser = new LineParser();

        //1st pass - verifies the global scope
        firstPass();
        System.out.println("DONE");
//        for (var variable : scopes.get(0).variablesMap.entrySet()) System.out.println(variable);

        //2nd pass - verifies the internal lines in each function
//        secondPass();
    }

    /**
     * Classifies each line as either comment/empty line, variable line, function line or bad line.
     * then calls corresponding function to verify the line itself is legal
     *
     * @throws IOException upon IO failure
     */
    private void firstPass() throws Exception {
        //add the global scope
        scopes.add(new Scope());

        //iterate over lines and create global variables and functions
        String line;
        int lineNumber = 0;
        while ((line = bufferedReader.readLine()) != null) {
            switch (parser.parseLineType(line)) {
                case COMMENT:
                    System.out.println("comment");
                    break;
                case VARIABLE:
                    extractVariables(line);  // validate the line, and save the variable
                    break;
                case FUNCTION: //todo how the new line is skipped to
                    saveFunctionAndMoveOn(line, lineNumber);  // save the function, move cursor to its end
                    break;
                default:
                    // global scope must contain only variables or functions
                    System.out.println("bad line: " + line);
//                    verificationFailed();
            }
            lineNumber++;
        }
    }

    /**
     * This function saves the name and start line of the function, and moves the cursor to the next
     *
     * @param line      the line itself
     * @param startLine the index of the start of the function
     * @throws IOException in case there is some issue
     */
    private void saveFunctionAndMoveOn(String line, int startLine) throws IOException {
        System.out.println("functions");
        // get function name from the line
        // TODO: implement
        String funcName = line;

        // add function to DB
        functions.put(funcName, startLine);

        // move cursor to the next line right after the function
        int numScopes = 0;
        while ((line = bufferedReader.readLine()) != null) {
            switch (parser.parseLineType(line)) {
                case IF:
                case WHILE:
                    numScopes++;
                case END_OF_SCOPE:
                    numScopes--;
                    if (numScopes == 0) {
                        return;
                    }
            }
        }
    }

    /**
     * This function is called when verification has failed due to syntax or logical problems.
     * It throws a proper exception.
     *
     * @throws Exception regarding the issue verification failed
     */
    public void verificationFailed() throws Exception {
        //TODO: update to throw whatever we want
        throw new Exception();
    }

    /**
     * This function iterates through all the functions found in the 1st pass, one by one, and for each function
     * validates all the internal lines of the function.
     *
     * @throws Exception throws a proper agreed upon exception if validation fails for some reason.
     */
    private void secondPass() throws Exception {
        //iterate over functions that were saved in the 1st pass
        for (HashMap.Entry<String, Integer> entry : functions.entrySet()) {

            // get the cursor to the start of the function
            for (int i = 0; i < entry.getValue(); ++i)
                bufferedReader.readLine();

            //parse the first line of the function
            String line = bufferedReader.readLine();
            scopes.add(new Scope());
            int numScopes = 1;
            parseFunctionLine(line);

            //parse all the inner lines of the function
            LineParser parser = new LineParser();
            int lineNum = 1;
            int lastReturnLineNum = -1;
            while ((line = bufferedReader.readLine()) != null) {
                switch (parser.parseLineType(line)) {
                    case COMMENT:
                        break;
                    case IF:
                    case WHILE:
                        scopes.add(new Scope());
                        numScopes++;
                        break;
                    case VARIABLE:
                        extractVariables(line);
                        break;
                    case FUNCTION_CALL:
                        parseFunctionCall(line);
                        break;
                    case RETURN:
                        lastReturnLineNum = lineNum;
                        break;
                    case END_OF_SCOPE:
                        //delete the last scope from the linked list of scopes
                        scopes.remove(scopes.size() - 1);
                        --numScopes;

                        //check if this was the closing } of the function scope
                        if (numScopes == 0) {
                            //verify that last line was a valid 'return;' statement
                            if (lastReturnLineNum != lineNum - 1) {
                                verificationFailed();
                            }
                        }
                        break;
                    case FUNCTION:  // it's invalid if there's another function definition
                    case UNRECOGNIZED:  // if none of the above, the line's invalid
                        verificationFailed();
                }
                lineNum++;
            }
        }
    }

    //TODO: implement
    private void parseFunctionCall(String line) throws Exception {
        // function call - method name exists, params should be - good types, initialized, same number
    }

    //TODO: implement
    private void parseFunctionLine(String line) throws IOException {
        //add parameters as local variables in the scope
        //init variables - final or not, with type and name
        System.out.println("FUNCTION PARSE");
    }

    /**
     * Validates (syntax-wise) a given variable line
     * @param line a given string represents the line
     * @throws IOException
     */
    private void extractVariables(String line) throws IOException {
        System.out.println(line);
        boolean isFinal = false;
        String type = "";
        //final and type
        Matcher isFinalAndTypeMatcher = parser.getFinalAndTypePattern().matcher(line);
        if(isFinalAndTypeMatcher.find()) {
            String[] isFinalAndType = isFinalAndTypeMatcher.group().replaceFirst("^\\s+", "").split("\\s+");
            isFinal = isFinalAndType.length == 2;
            type = isFinalAndType.length == 2 ? isFinalAndType[1] : isFinalAndType[0];
        }

        System.out.println("type: " + type + ". is final? " + isFinal);
        //get all variables
        //todo problem with first word
        //todo problem with only declaring
        Matcher variableMatcher = parser.getVariablesPattern().matcher(line);
        while(variableMatcher.find()) {
            String[] possibleVariable = variableMatcher.group().replaceFirst("^\\s+", "").replaceAll("[=,]"," ").split("\\s+");
            for(var word : possibleVariable) System.out.println(word);
            System.out.println("DONE WITH VAR");
        }
        //flow
        //as long as the matcher find a legal variable:
        //validate final vs assignment
        //validate declared type vs actual value

//        scopes.get(scopes.size() - 1).addVariable(newVariable);
    }

    private boolean isTypeMatchesValue(Variable variable) {
        return true;
    }
}