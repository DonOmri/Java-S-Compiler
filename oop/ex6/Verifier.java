package oop.ex6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verifier {

    //holds all relevant data for each scope, including the global scope (at index 0)
    private final ArrayList<Scope> scopes = new ArrayList<>();
    //holds the function names and line number
    private final HashMap<String, Function> functions = new HashMap<>();
    private BufferedReader bufferedReader;
    private String file;
    private LineParser parser;

    /**
     * This function receives a path to a sJavac file, and verifies its validity.
     *
     * @param file a path to the sJavac file to check
     * @throws Exception in case of problems
     */
    public void verifySjavacFile(String file) throws Exception {
        this.file = file;
        this.bufferedReader = new BufferedReader(new FileReader(this.file)); // remember to close
        this.parser = new LineParser();

        //1st pass - verifies the global scope
        System.out.println("\n****************************************");
        System.out.println("      first pass - global scope");
        System.out.println("****************************************\n");
        firstPass();
        System.out.println("variables:");
        for (var variable : scopes.get(0).variablesMap.entrySet()) System.out.println(variable);
        System.out.println("\nfunctions: (num of params should be zero)");
        functions.forEach((key, value) -> System.out.println(key + " : " + value));

        //2nd pass - verifies the internal lines in each function
        System.out.println("\n****************************************");
        System.out.println("    second pass - inside functions");
        System.out.println("****************************************\n");
        secondPass();
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
                case EMPTY:
                    break;  // ignore comments or empty lines
                case VARIABLE:
                    extractVariables(line);  // validate the line, and save the variable
                    break;
                case FUNCTION:
                    lineNumber = saveFunctionNameAndParams(line, lineNumber);  // save function & update cursor
                    break;
                default:
                    verificationFailed("global scope contained something that's not variables or function: " + parser.parseLineType(line));
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
    private int saveFunctionNameAndParams(String line, int startLine) throws Exception {
        // get function name
        String functionName = parser.getFunctionName(line);

        // add function to DB, unless it's already there which is wrong, so exit
        if (functions.containsKey(functionName)) {
            verificationFailed("function name already exists");
        }
        functions.put(functionName, new Function(startLine));

        // regex to match function declaration
        Matcher matcher = Pattern.compile("\\w+\\s+\\w+\\s*\\(([^)]*)\\)").matcher(line);
        if (!matcher.find()) {
            verificationFailed("invalid structure of function declaration");
        }

        // else, get the parameters and split them by comma
        String[] params = matcher.group(1).split(",");

        // if there are any params, parse them into a variable and save them
        if (!(params.length == 1 && params[0].equals(""))) {
            // otherwise, trim whitespace and add each parameter to results
            for (String param : params) {
                String[] parts = param.trim().split("\\s+");
                String type = parts[0];
                String name = parts[1];
                boolean isFinal = false;
                if (parts.length > 2) {
                    isFinal = true;
                    type = parts[1];
                    name = parts[2];
                }
                Variable var = new Variable(type, name, isFinal);
                functions.get(functionName).parameters.add(var);  // add to function's parameters
            }
        }

        // move cursor to the next line right after the function declaration
        int numScopes = 1, lineNumber = startLine;
        while ((line = bufferedReader.readLine()) != null) {
            switch (parser.parseLineType(line)) {
                case IF:
                case WHILE:
                    numScopes++;
                    break;
                case END_OF_SCOPE:
                    numScopes--;
                    if (numScopes == 0) {
                        return lineNumber + 1;
                    }
                    break;
            }
            lineNumber++;
        }

        // in case we got here, the function never ended, so the verification fails and we return some
        // garbage value
        verificationFailed("function that started in line" + startLine + " never ended");
        return -1;
    }

    /**
     * This function is called when verification has failed due to syntax or logical problems.
     * It throws a proper exception.
     *
     * @throws Exception regarding the issue verification failed
     */
    public void verificationFailed(String reason) throws Exception {
        //TODO: update to throw whatever we want
        System.err.println("Verification failed: " + reason);
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
        for (HashMap.Entry<String, Function> entry : functions.entrySet()) {

            // get the cursor to the start of the function
            this.bufferedReader = new BufferedReader(new FileReader(this.file));
            for (int i = 0; i < entry.getValue().startLine; ++i)
                bufferedReader.readLine();

            //parse the first line of the function
            String line = bufferedReader.readLine();
            scopes.add(new Scope());
            int numScopes = 1;
            addParamsToLocalScope(line);  //insert parameters of the function to the scope

            //parse all the inner lines of the function
            LineParser parser = new LineParser();
            int lineNum = 1;
            int lastReturnLineNum = -1;
            boolean next_function = false;
            while ((line = bufferedReader.readLine()) != null) {
                switch (parser.parseLineType(line)) {
                    case COMMENT:
                    case EMPTY:
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
                            //if last line was a return, continue to next function
                            if (lastReturnLineNum == lineNum - 1) {
                                System.out.println("function " + entry.getKey() + "() is valid");
                                next_function = true;
                            } else {
                                //invalid
                                verificationFailed("last line of the function wasn't a return");
                            }
                        }
                        break;
                    case FUNCTION:  // it's invalid if there's another function definition
                    case UNRECOGNIZED:  // if none of the above, the line's invalid
                        verificationFailed("line type wasn't recognized at line " + lineNum);
                }
                if (next_function) break;
                lineNum++;
            }
        }
    }

    /**
     * This function receives a line classified as a function call, check the validity of its contents -
     * 1) the name of the function exists
     * 2) the parameters sent indeed fit the parameters expected
     *
     * @param line the function call line
     * @throws Exception if problem
     */
    private void parseFunctionCall(String line) throws Exception {

        // Regular expression to match function call
        String regex = "([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(([^)]*)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {

            //check that the function exists
            String functionName = matcher.group(1);
            if (!functions.containsKey(functionName)) {
                verificationFailed("invalid function call - function that doesn't exist");
            }
            Function function = functions.get(functionName);

            // get the parameters (split by comma and remove spaces)
            String[] params = matcher.group(2).split(",");
            for (int i = 0; i < params.length; i++) {
                params[i] = params[i].trim();

                //params[i] should be a valid assignment for function.parameters[i]
                if (function.parameters.size() <= i) {
                    verificationFailed("invalid function call - wrong number of parameters given");
                }

                //decide if it is a variable or a value - search for the variable name in all scopes
                //then, try to assign the i'th parameter of the function with this value/parameter
                boolean found = false;
                boolean success = false;
                for (int j = scopes.size() - 1; j >= 0; j--) {
                    if (scopes.get(j).variablesMap.containsKey(params[i])) {
                        found = true;  // it's a variable in  some outer scope
                        Variable param = scopes.get(j).variablesMap.get(params[i]);  // get the variable
                        success = function.parameters.get(i).assign(param);  // try to make the assignment
                    }
                }
                if (!found) {
                    success = function.parameters.get(i).assign(params[i]);
                }
                if (!success) {
                    verificationFailed("invalid function call - bad parameters given");
                }
            }
        }
    }

    /**
     * This function receives a line classified as a declaration of a function, saves the parameters as
     * variables in the current scope (that was just opened) and adds them to the functions map for future
     * calls.
     *
     * @param line the line
     * @throws Exception in case of a problem
     */
    private void addParamsToLocalScope(String line) throws Exception {
        // get function name
        String functionName = parser.getFunctionName(line);

        for (Variable var : functions.get(functionName).parameters) {
            // try to add param to local scope
            boolean success = scopes.get(scopes.size() - 1).addVariable(var);
            if (!success) {  //this scope already has a variable with this name, which is wrong
                verificationFailed("function parameters with the same name");
            }
        }
    }

    /**
     * @param line a syntax-wise validated line
     * @throws IOException if a final variable wasn't assigned or a value don't match declared variable type
     */
    private void extractVariables(String line) throws Exception { //todo currently dont throw exceptions, printerr instead
        boolean isFinal = false;
        String type = "";
        //final and type
        Matcher isFinalAndTypeMatcher = parser.getFinalAndTypePattern().matcher(line);
        if (isFinalAndTypeMatcher.find()) {
            String[] isFinalAndType = isFinalAndTypeMatcher.group().replaceFirst("^\\s+", "").split("\\s+");
            isFinal = isFinalAndType[0].equals("final");
            type = isFinal ? isFinalAndType[1] : isFinalAndType[0];
        }

        String variablesAsString = line.substring(isFinalAndTypeMatcher.end());
        System.out.println(variablesAsString);

        Matcher variableMatcher = parser.getVariablesPattern().matcher(variablesAsString);

        //as long as you've found a correct syntax of a variable
        while (variableMatcher.find()) {
            //split it to crucial parts, and remove everything else
            String[] varFragments = variableMatcher.group().replaceFirst("^\\s*", "").replaceAll("[,;=\\s]+", " ").split("\\s+");

            if (varFragments.length > 0) for (var word : varFragments) System.out.println(word);

            //if there was only one parts - no assignment occurred
            if (varFragments.length == 1) {
                if (isFinal) System.err.println("err - final wasn't assigned");
                else scopes.get(scopes.size() - 1).addVariable(new Variable(type, varFragments[0], false));
            }
            //assignment occurred - compare type and value
            else if (varFragments.length == 2) {
                if (!compareTypeAndValue(type, varFragments[1]))
                    System.err.println("err  - type and value don't match");
                else scopes.get(scopes.size() - 1).addVariable(new Variable(type, varFragments[0], isFinal));
            } else System.out.println("SOMETHING WEIRD HAPPENED");
        }
    }

    private boolean compareTypeAndValue(String type, String value) { //todo

        return true;
    }
}