package oop.ex6;

import oop.ex6.Exceptions.*;
import oop.ex6.Exceptions.FunctionExceptions.DoubleFunctionDeclarationException;
import oop.ex6.Exceptions.FunctionExceptions.UnendingFunctionException;
import oop.ex6.Exceptions.VariableExceptions.DoubleVariableDeclarationException;
import oop.ex6.Exceptions.VariableExceptions.FinalVariableException;
import oop.ex6.Exceptions.VariableExceptions.TypeValueMismatchException;
import oop.ex6.Exceptions.VariableExceptions.VariableException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for verifying internal logic of a line
 */
public class Verifier {
    private static final int NONE = 0;
    private static final int NO_ASSIGNMENT_LENGTH = 1;
    private static final int ASSIGNMENT_LENGTH = 2;
    private static final int VAR_NAME_LOCATION = 0;
    private static final int VAR_VALUE_LOCATION = 1;
    private static final int VAR_FINAL_LOCATION = 0;
    private static final int VAR_TYPE_LOCATION = 1;
    private static final int FUNC_VAR_NAME_LOCATION = 2;
    private final ArrayList<Scope> scopes = new ArrayList<>();
    private final HashMap<String, Function> functions = new HashMap<>();
    private BufferedReader bufferedReader;
    private String file;
    private LineParser parser;

    /**
     * This function receives a path to a sJavac file, and verifies its validity.
     *
     * @param file a path to the sJavac file to check
     * @throws JavacException if there was a problem with a line
     * @throws IOException if there was a problem with a file
     */
    public void verifySjavacFile(String file) throws JavacException, IOException {
        this.file = file;
        this.bufferedReader = new BufferedReader(new FileReader(this.file));
        this.parser = new LineParser();

        verifyGlobalScope(); //Verifies global scope
        verifyInnerScopes(); //verifies internal function lines

//        printAllVariables();

        bufferedReader.close(); //todo move to try with resources
    }

    private void printAllVariables(){ //TODO DELETE!
        System.out.println("variables:");
        for (var variable : scopes.get(0).variablesMap.entrySet()) System.out.println(variable);
        System.out.println("\nfunctions: (num of params should be zero)");
        functions.forEach((key, value) -> System.out.println(key + " : " + value));
    }

    /**
     * Classifies each line as either comment/empty line, variable line, function line or bad line.
     * then calls corresponding function to verify that the line itself is legal, and creates variables and
     * functions correspondingly
     *
     * @throws JavacException if line could npt be recognized, or had bad logic in it
     * @throws IOException upon IO failure
     */
    private void verifyGlobalScope() throws JavacException, IOException {
        scopes.add(new Scope());

        String line;
        int lineNumber = 0;
        while ((line = bufferedReader.readLine()) != null) {
            switch (parser.parseLineType(line)) {
                case EMPTY: break; //ignore empty line
                case COMMENT: break; //ignore comment line
                case VARIABLE: //validate the line logic, then save variables in scope
                    extractVariables(line);
                    break;
                case POSSIBLE_ASSIGN: //validate line logic
                    checkAssignmentForLine(line);
                    break;
                case FUNCTION: //validate line logic, then save function and move to end of function
                    lineNumber = saveFunctionReference(line, lineNumber);
                    break;
                default: throw new BadLineException(line);
            }
            lineNumber++;
        }
    }

    /**
     * This function saves the name and start line of the function, and moves the cursor to the next
     * @param line the line itself
     * @param startLine function declaration line
     * @throws JavacException if there was a logic failure in the function declaration line
     * @throws IOException if bufferedreader could not read a line for some reason
     */
    private int saveFunctionReference(String line, int startLine) throws JavacException, IOException {
        String functionName = parser.getFunctionName(line);

        if (functions.containsKey(functionName)) throw new DoubleFunctionDeclarationException(functionName);

        functions.put(functionName, new Function(startLine));

        Matcher matcher = parser.getFunctionLinePattern().matcher(line);
        String[] params = matcher.group(1).split(",");
        if (params.length != 1 || !params[0].equals("")) addFunctionLineParameters(functionName, params);

        return skipFunction(startLine);

    }

    /**
     * Validates logic of parameters declaration in function declaration, and adds them into function scope
     * @param functionName name of the function
     * @param params parameters from within the brackets
     * @throws JavacException if could not create a new variable for some reason
     */
    private void addFunctionLineParameters(String functionName, String[] params) throws JavacException{
        for (String param : params) {
            String[] parts = param.trim().split("\\s+");

            boolean isFinal = parts.length > 2;
            String type = parts[isFinal ? VAR_TYPE_LOCATION : VAR_TYPE_LOCATION - 1];
            String name = parts[isFinal ? FUNC_VAR_NAME_LOCATION : FUNC_VAR_NAME_LOCATION - 1];

            Variable var = new Variable(type, name, isFinal);

            functions.get(functionName).parameters.add(var);  // add to function's parameters
        }
    }

    /**
     * Skips to an end of declared function;
     * @param startLine the starting line of the function
     * @return the line number after the end of the function
     * @throws IOException if could not read a line from file for some reason
     * @throws UnendingFunctionException if function was never closed using '}' character
     */
    private int skipFunction(int startLine) throws IOException, UnendingFunctionException{
        String line;
        int addedScopes = 1, lineNumber = startLine;
        while ((line = bufferedReader.readLine()) != null) {
            switch (parser.parseLineType(line)) {
                case IF:
                case WHILE:
                    ++addedScopes;
                    break;
                case END_OF_SCOPE:
                    --addedScopes;
                    if (addedScopes == NONE) return lineNumber + 1;
                    break;
            }
            ++lineNumber;
        }

        throw new UnendingFunctionException(startLine);
    }

    /**
     * This function is called when verification has failed due to syntax or logical problems.
     * It throws a proper exception.
     *
     * @throws JavacException regarding the issue verification failed
     */
    public void verificationFailed(String reason) throws JavacException {
        throw new JavacException("Verification failed: " + reason);
    }

    /**
     * This function iterates through all the functions found in the 1st pass, one by one, and for each function
     * validates all the internal lines of the function.
     *
     * @throws JavacException if there was a problem with any inner line
     * @throws IOException if problem with any file
     */
    private void verifyInnerScopes() throws JavacException, IOException {
        //iterate over functions that were saved in the 1st pass
        for (var entry : functions.entrySet()) {

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
                        ++numScopes;
                        break;
                    case VARIABLE:
                        extractVariables(line);
                        break;
                    case FUNCTION_CALL:
                        verifyFunctionCall(line);
                        break;
                    case RETURN:
                        lastReturnLineNum = lineNum;
                        break;
                    case END_OF_SCOPE:
                        //delete the last scope from the linked list of scopes
                        scopes.remove(scopes.size() - 1);

                        //check if this was the closing } of the function scope
                        if (--numScopes == 0) {
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
                    case POSSIBLE_ASSIGN:
                        checkAssignmentForLine(line);
                        break;
                    case FUNCTION:  // it's invalid if there's another function definition
                    case UNRECOGNIZED: throw new BadLineException(line);
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
     * @throws JavacException if couldn't verify function
     */
    private void verifyFunctionCall(String line) throws JavacException {

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
     * @throws DoubleVariableDeclarationException in case of a problem
     */
    private void addParamsToLocalScope(String line) throws DoubleVariableDeclarationException {
        String functionName = parser.getFunctionName(line);

        for (Variable var : functions.get(functionName).parameters) {
            // try to add param to local scope
            if (!scopes.get(scopes.size() - 1).addVariable(var))
                throw new DoubleVariableDeclarationException(var.getName());
            }
    }

    /**
     * extracts new variables from a declaration line
     * @param line a syntax-wise validated line
     * @throws VariableException if there was a problem assigning value to a variable
     */
    private void extractVariables(String line) throws VariableException {
        Matcher isFinalAndTypeMatcher = parser.getFinalAndTypePattern().matcher(line);

        String[] finalAndTypeAsString = extractFinalAndType(isFinalAndTypeMatcher);
        boolean isFinal = finalAndTypeAsString[VAR_FINAL_LOCATION].equals("true");
        String type = finalAndTypeAsString[VAR_TYPE_LOCATION];

        Matcher variableMatcher = parser.getVariablesPattern().matcher(
                line.substring(isFinalAndTypeMatcher.end()));

        while (variableMatcher.find()) extractSingleVariable(variableMatcher, isFinal, type);
    }

    /**
     * Detects the type of the declared variables in line, and whether they are final or not
     * @param matcher the matcher that holds the pattern for type and final
     * @return a string containing the type of the variables, and whether they are final or not
     */
    private String[] extractFinalAndType(Matcher matcher){
        String[] finalAndType = new String[2];
        if (matcher.find()) {
            String[] isFinalAndType = matcher.group().replaceFirst("^\\s+", "").split("\\s+");
            finalAndType[0] = isFinalAndType[0].equals("final") ? "true" : "false";
            finalAndType[1] = finalAndType[0].equals("true") ? isFinalAndType[1] : isFinalAndType[0];
        }

        return finalAndType;
    }

    /**
     * Extracts a single variable from the line
     * @param varMatcher the matcher that detects variables
     * @param isFinal whether the variable final or not
     * @param type type of the variable
     * @throws VariableException if there was a problem creating a variable
     */
    private void extractSingleVariable(Matcher varMatcher, boolean isFinal, String type)
            throws VariableException {
        String[] varFragments = varMatcher.group().replaceFirst("^\\s*", "")
                .replaceAll("[,;=\\s]+", " ").split("\\s+");

        VerifyVariableNotInScope(varFragments[VAR_NAME_LOCATION]);

        //if there was only one part - no assignment occurred
        if (varFragments.length == NO_ASSIGNMENT_LENGTH) {
            if (isFinal) throw new FinalVariableException(true);
            scopes.get(scopes.size() - 1).addVariable(
                    new Variable(type, varFragments[VAR_NAME_LOCATION], false));
        }
        //assignment occurred - compare type and value
        else if (varFragments.length == ASSIGNMENT_LENGTH) {
            var newVar = new Variable(type, varFragments[VAR_NAME_LOCATION], isFinal);
            if (!newVar.assign(varFragments[VAR_VALUE_LOCATION]) && !ValidateAssignment(varFragments, type))
                throw new TypeValueMismatchException(varFragments);
            else scopes.get(scopes.size() - 1).addVariable(newVar);
        }
    }

    /**
     * Verifies a declared variable has not been alreay declared in current scope
     * @param name variable name
     * @throws DoubleVariableDeclarationException if the variable was already declared at current scope
     */
    private void VerifyVariableNotInScope(String name) throws DoubleVariableDeclarationException{
        if (scopes.get(scopes.size() - 1).variablesMap.containsKey(name))
            throw new DoubleVariableDeclarationException(name);
    }

    /**
     * Checks assignment for an assignment line
     * @param line a line to check
     */
    private void checkAssignmentForLine(String line)
            throws TypeValueMismatchException, FinalVariableException{
        Matcher variableMatcher = parser.getVariablesPattern().matcher(line);
        while (variableMatcher.find()){
            String[] varFragments = variableMatcher.group().replaceFirst("^\\s*", "").
                    replaceAll("[,;=\\s]+", " ").split("\\s+");

            ValidateAssignment(varFragments, GetValueType(varFragments[0]));
        }
    }

    /**
     * Checks assignment for one variable
     * @param varFragments the assigned variable and assignee
     * @return true if assignment succeeded, false otherwise
     */
    private boolean ValidateAssignment(String[] varFragments, String assignedType)
            throws TypeValueMismatchException, FinalVariableException
    {
        String assigned = varFragments[0], assignee = varFragments[1];
        String assigneeType = GetValueType(assignee);

        for (int i = scopes.size()-1; i>= 0; --i){
            var scopeVariables = scopes.get(i).variablesMap;

            if (assignedType.equals("") && scopeVariables.containsKey(assigned)) //check first var in scope
                assignedType = scopeVariables.get(assigned).getType();

            if (assigneeType.equals("") && scopeVariables.containsKey(assignee)) //check second var in scope
                assigneeType = scopeVariables.get(assignee).getType();

            if (!assignedType.equals("") && !assigneeType.equals("") && assignedType.equals(assigneeType)){
                if (scopeVariables.get(assigned) != null && scopeVariables.get(assigned).getIsFinal())
                    throw new FinalVariableException(false);
                return true;
            }

        }

        throw new TypeValueMismatchException(varFragments);
    }

    /**
     * Checks type of the possible value
     * @param assignee the possible value as string
     * @return the value type, or "" if nothing fits
     */
    private String GetValueType(String assignee){
        if(parser.getIntValuesPattern().matcher(assignee).matches()) return "int";
        else if(parser.getDoubleValuesPattern().matcher(assignee).matches()) return "double";
        else if(parser.getBooleanValuesPattern().matcher(assignee).matches()) return "boolean";
        else if(parser.getStringValuesPattern().matcher(assignee).matches()) return "String";
        else if(parser.getCharValuesPattern().matcher(assignee).matches()) return "char";
        else return ""; //if nothing fits
    }
}