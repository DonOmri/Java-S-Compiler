package oop.ex6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        this.bufferedReader = new BufferedReader(new FileReader(file));
        this.parser = new LineParser();

        //1st pass - verifies the global scope
        firstPass();
        System.out.println("DONE");
        for (var variable : scopes.get(0).variablesMap.entrySet()) System.out.println(variable);

        //2nd pass - verifies the internal lines in each function
        secondPass();
    }

    /**
     * Classify each line as either comment/empty line, variable line, function line or bad line. then calls
     * to corresponding function to verify the line itself is legal
     *
     * @throws IOException upon IO failure
     */
    private void firstPass() throws Exception {
        //add the global scope
        scopes.add(new Scope());

        //start iterating over lines - 1st pass
        String line;
        int lineNumber = 0;
        while ((line = bufferedReader.readLine()) != null) {
            switch (parser.getLineType(line)) {
                case COMMENT:
                    break;
                case VARIABLE:
                    parseVariableLine(line);  // validate the line, and save the variable
                    break;
                case FUNCTION:
                    saveFunctionAndMoveOn(line, lineNumber);  // save the function, move cursor to its end
                    break;
                default:
                    // global scope must contain only variables or functions
                    System.out.println("bad line: " + line);
                    verificationFailed();
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
        // get function name from the line
        // TODO: implement
        String funcName = line;

        // add function to DB
        functions.put(funcName, startLine);

        // move cursor to the next line right after the function
        int numScopes = 0;
        while ((line = bufferedReader.readLine()) != null) {
            switch (parser.getLineType(line)) {
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
                switch (parser.getLineType(line)) {
                    case COMMENT:
                        break;
                    case IF:
                    case WHILE:
                        scopes.add(new Scope());
                        numScopes++;
                        break;
                    case VARIABLE:
                        parseVariableLine(line);
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

    private void parseVariableLine(String line) throws IOException {
        List<String> words = Arrays.asList(line.replaceFirst("^\\s+", "").split("\\s+"));

        Variable newVariable = words.get(0).equals("final") ?
                new Variable(words.get(1), words.get(2), true) :
                new Variable(words.get(0), words.get(1), false);

        if (words.get(0).equals("final")) { //check if final is assigned

            if (words.size() != 5 || !words.get(3).equals("=")) System.out.println("wtf " + line);
            ;
        }

        scopes.get(scopes.size() - 1).addVariable(newVariable);
    }

    private boolean isTypeMatchesValue(Variable variable) {
        return true;
    }
}