package oop.ex6.main;

import oop.ex6.main.FunctionExceptions.*;
import oop.ex6.main.VariableExceptions.*;
import oop.ex6.utils.Function;
import oop.ex6.utils.LineType;
import oop.ex6.utils.Scope;
import oop.ex6.utils.Variable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Responsible for verifying internal logic of a line
 */
public class Verifier {
    private static final String DOUBLE = "double";
    private static final String INT = "int";
    private static final String BOOLEAN = "boolean";
    private static final String STRING = "String";
    private static final String CHAR = "char";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String FINAL = "final";
    private static final int FUNCTION_END_CUE = -2;
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
     * @throws SjavacException if there was a problem with a line
     * @throws IOException if there was a problem with a file
     */
    public void verifySjavacFile(String file, BufferedReader bufferedReader)
            throws SjavacException, IOException {
        this.file = file;
        this.bufferedReader = bufferedReader;
        this.parser = new LineParser();

        verifyGlobalScope();
        System.out.println("GLOBAL SCOPE VERIFIED"); //todo DELETE
        verifyInnerScopes(); //verifies internal function lines

        printAllVariables();
    }

    private void printAllVariables(){ //TODO DELETE!
        System.out.println("variables:");
        for (var variable : scopes.get(0).getVariablesMap().entrySet()) System.out.println(variable);
        System.out.println("\nfunctions: (num of params should be zero)");
        functions.forEach((key, value) -> System.out.println(key + " : " + value));
    }

    public void testsFunctionHelper(String file, BufferedReader bufferedReader){ //todo DELETE
        this.file = file;
        this.bufferedReader = bufferedReader;
        this.parser = new LineParser();
    }

    public void testsFunction() throws IOException{ //todo DELETE
        /***********************GLOBAL SCOPE***********************/
        int failedTests = 0;
        scopes.add(new Scope());

        String line;
        int lineNum = 0;

        while ((line = bufferedReader.readLine()) != null)
            try{
                lineNum = globalScopeFactory(line, lineNum) + 1;
                System.out.println("Test passed in line " + lineNum);
            }
            catch (SjavacException e){
//                System.err.println("in line: " + ++lineNum + ": " + e.getClass());
                ++lineNum;
                ++failedTests;
            }
        /***********************INNER SCOPES***********************/
        var failedInnerLines = 0;
        var innerLines = 0;
        for (var entry : functions.entrySet()) {
            scopes.add(new Scope()); //add function scope

            bufferedReader = new BufferedReader(new FileReader("test_files/" + this.file));
            for (int i = 0; i < entry.getValue().getStartLine(); ++i) {bufferedReader.readLine();}

            String line2 = bufferedReader.readLine(); //get function declaration line
            try{
                addParamsToLocalScope(parser.getFunctionName(line2));  //add function parameters to it's scope
            }
            catch (SjavacException e){
//                System.err.println(e.getMessage());
            }

            int lineNum2 = 1, lastReturnLineNumber = -1;
            while ((line2 = bufferedReader.readLine()) != null) {
                try{
                    innerLines++;
                    lastReturnLineNumber = innerScopesFactory(line2, lineNum2++, lastReturnLineNumber);
                    if (lastReturnLineNumber == FUNCTION_END_CUE) break;
                }
                catch (SjavacException e){
                    failedInnerLines++;
                    System.err.println("INNER SCOPE FAIL in line: " + (lineNum2 + entry.getValue().getStartLine()) + ": " + e.getClass());
                }

            }
        }
        /*********************************************************/
        System.out.println("***************************************\n\t\t\tTotal lines: " + lineNum +
                "\n\t\t\tValid lines: " + (lineNum - failedTests) + "\n\t\t\tInvalid lines: " + failedTests +
                "\nInner lines failed: " + failedInnerLines +
                " from total of " + innerLines + "\n***************************************\n");
    }

    /**
     * Classifies each line as either comment/empty line, variable line, function line or bad line.
     * then calls corresponding function to verify that the line itself is legal, and creates variables and
     * functions correspondingly
     *
     * @throws SjavacException if line could npt be recognized, or had bad logic in it
     * @throws IOException upon IO failure
     */
    private void verifyGlobalScope() throws SjavacException, IOException {
        scopes.add(new Scope());

        String line;
        int lineNum = 0;

        while ((line = bufferedReader.readLine()) != null) lineNum = globalScopeFactory(line, lineNum) + 1;
    }

    /**
     * Iterates over functions found in verifyGlobalScope, and verifies their inner logic
     * @throws SjavacException for any inner-line bad syntax or bad logic
     * @throws IOException if bufferedReader could not read a line for any reason
     */
    private void verifyInnerScopes() throws SjavacException, IOException {
        for (var entry : functions.entrySet()) {
            scopes.add(new Scope()); //add function scope

            setBufferedReaderLine(entry); // get bufferedReader to start of function
            String line = bufferedReader.readLine(); //get function declaration line
            addParamsToLocalScope(parser.getFunctionName(line));  //add function parameters to it's scope

            int lineNum = 1, lastReturnLineNumber = -1;
            while ((line = bufferedReader.readLine()) != null) {
                lastReturnLineNumber = innerScopesFactory(line, lineNum++, lastReturnLineNumber);
                if (lastReturnLineNumber == FUNCTION_END_CUE) break;
            }
        }
    }

    /**
     * Saves function name and start line, and moves the cursor to line after function closing curly bracket
     * @param line the function declaration line
     * @param startLine function declaration line
     * @throws SjavacException if there was a logic failure in the function declaration line
     * @throws IOException if bufferedreader could not read a line for some reason
     */
    private int saveFunctionReference(String line, int startLine) throws SjavacException, IOException {
        String functionName = parser.getFunctionName(line);
        if (functions.containsKey(functionName)) throw new DoubleFunctionDeclarationException(functionName);

        functions.put(functionName, new Function(startLine));
        Matcher matcher = parser.getFunctionLinePattern().matcher(line);
        if(!matcher.find()) throw new BadLineException(line);

        String[] params = matcher.group(1).split(",");
        if (params.length != 1 || !params[0].equals("")) addFunctionLineParameters(functionName, params);

        return skipFunction(startLine);
    }

    /**
     * Validates parameters logic in function declaration line, and adds them to the function
     * (to verify later calls to it)
     * @param functionName name of the function
     * @param params parameters from within the brackets
     * @throws SjavacException if could not create a new variable for some reason
     */
    private void addFunctionLineParameters(String functionName, String[] params) throws SjavacException{
        for (String param : params) {
            String[] parts = param.trim().split("\\s+");

            boolean isFinal = parts.length > 2;
            String type = parts[isFinal ? VAR_TYPE_LOCATION : VAR_TYPE_LOCATION - 1];
            String name = parts[isFinal ? FUNC_VAR_NAME_LOCATION : FUNC_VAR_NAME_LOCATION - 1];

            Variable var = new Variable(type, name, isFinal, true);

            functions.get(functionName).getFunctionParameters().add(var);  // add to function's parameters
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
            LineType lineType = parser.parseLineType(line);

            if(lineType.equals(LineType.IF_WHILE)) ++addedScopes;
            else if (lineType.equals(LineType.END_OF_SCOPE))
                if (--addedScopes == NONE) return lineNumber + 1;

            ++lineNumber;
        }
        throw new UnendingFunctionException(startLine); //the first scope was never closed
    }

    /**
     * Moves the buffer to start of a given function
     * @param entry the name-function pair of relevant function
     * @throws IOException if bufferedReader could not read line for some reason
     */
    private void setBufferedReaderLine(Map.Entry<String, Function> entry) throws IOException{
        bufferedReader = new BufferedReader(new FileReader(this.file));
        for (int i = 0; i < entry.getValue().getStartLine(); ++i) bufferedReader.readLine();
    }


    /**
     * Validates a function call line - syntax and logic wise (function name exists and parameters correct)
     * @param line the function call line
     * @throws SjavacException if function could not be verified for any reason
     */
    private void verifyFunctionCall(String line) throws SjavacException {
        Matcher functionCallMatcher = parser.getFunctionCallLinePattern().matcher(line);

        if (functionCallMatcher.find()){
            var nameAndVariables = functionCallMatcher.group().
                    replaceAll("^\\s*|(\\s*\\)\\s*;\\s*)$" ,"").split("\\(", 2);

            Function calledFunction = functions.get(nameAndVariables[0]);
            if (calledFunction == null) throw new NoFunctionException(line);

            String[] params = nameAndVariables[1].trim().split(",");
            if(params[0].equals("")) params = new String[]{};

            if (calledFunction.getFunctionParameters().size() != params.length)
                throw new CallParametersAmountException(line);

            //find out if the calling parameter is other variable or a value
            for (int i = 0; i < params.length; i++) {
                params[i] = params[i].trim();
                var assignee = getValueType(params[i]);

                if (!assignee.equals("")) { //calling param is a value
                    var funcParameter = calledFunction.getFunctionParameters().get(i);
                    validateAssignment(
                            new String[]{funcParameter.getName(), params[i]}, funcParameter.getType(), true);
                }
                else matchFunctionCallParameter(calledFunction, i, params, line); //calling param is another param
            }
        }
    }

    /**
     * Compares one parameter from function call with the original function parameter
     * @param function the function from which to get variables
     * @param paramNum parameter place in order (to get correct parameter from function - order matters)
     * @param params all current line parameters
     * @param line the current line as string
     * @throws CallParametersTypeException if there was a type mismatch in the call
     * @throws VariableNotFoundException if the parameter is a variable that was never declared
     */
    private void matchFunctionCallParameter(Function function, int paramNum, String[] params, String line)
            throws CallParametersTypeException, VariableNotFoundException{
        for (int curScopeIndex = scopes.size() - 1; curScopeIndex >= 0; --curScopeIndex) {
            Variable param;
            if ((param = scopes.get(curScopeIndex).getVariablesMap().get(params[paramNum])) != null) {
                if(!function.getFunctionParameters().get(paramNum).assign(param))
                    throw new CallParametersTypeException(line);
                return;
            }
        }
        throw new VariableNotFoundException(params[paramNum]);
    }

    /**
     * Receives a function name and adds its parameters to correct scope
     * @param functionName name of the function to add parameters to
     * @throws DoubleVariableDeclarationException if a variable with same name was already declared in scope
     */
    private void addParamsToLocalScope(String functionName) throws DoubleVariableDeclarationException {
        for (var variable : functions.get(functionName).getFunctionParameters()) {
            if (!scopes.get(scopes.size() - 1).addVariable(variable))
                throw new DoubleVariableDeclarationException(variable.getName());
        }
    }

    /**
     * extracts new variables from a declaration line
     * @param line a syntax-wise validated line
     * @throws VariableException if there was a problem assigning value to a variable
     */
    private void extractVariables(String line, boolean fromWithin)
            throws VariableException, BadLineException {
        Matcher isFinalAndTypeMatcher = parser.getFinalAndTypePattern().matcher(line);

        String[] finalAndTypeAsString = extractFinalAndType(isFinalAndTypeMatcher);
        boolean isFinal = finalAndTypeAsString[VAR_FINAL_LOCATION].equals(TRUE);
        String type = finalAndTypeAsString[VAR_TYPE_LOCATION];

        Matcher variableMatcher = parser.getVariablesPattern().
                matcher(line.substring(isFinalAndTypeMatcher.end()));

        if (!variableMatcher.find()) throw new BadLineException(line);
        do{
            extractSingleVariable(variableMatcher, isFinal, type, fromWithin);
        }
        while (variableMatcher.find());
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
            finalAndType[VAR_FINAL_LOCATION] = isFinalAndType[0].equals(FINAL) ? TRUE : FALSE;
            finalAndType[VAR_TYPE_LOCATION] = finalAndType[VAR_FINAL_LOCATION].equals(TRUE) ?
                    isFinalAndType[1] : isFinalAndType[0];
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
    private void extractSingleVariable(Matcher varMatcher, boolean isFinal, String type, boolean fromWithin)
            throws VariableException {
        String[] varFragments = getFragmentsByType(varMatcher.group(), type);

        VerifyVariableNotInScope(varFragments[VAR_NAME_LOCATION]);

        //if there was only one part - no assignment occurred
        if (varFragments.length == NO_ASSIGNMENT_LENGTH) {
            if (isFinal) throw new FinalVariableException(true);
            scopes.get(scopes.size() - 1).addVariable(
                    new Variable(type, varFragments[VAR_NAME_LOCATION], false, false));
        }
        //assignment occurred - compare type and value
        else if (varFragments.length == ASSIGNMENT_LENGTH) {
            var newVar = new Variable(type, varFragments[VAR_NAME_LOCATION], isFinal, true);

            if (!newVar.assign(varFragments[VAR_VALUE_LOCATION]) &&
                    !validateAssignment(varFragments, type, fromWithin))
                throw new TypeValueMismatchException(varFragments);
            else scopes.get(scopes.size() - 1).addVariable(newVar);
        }
    }

    /**
     * Splits the line correctly by the variable type
     * @param varLine the line to split
     * @param type the type of the variable
     * @return the fragments of the line, if succeeded
     * @throws ExtraCommasException if there was more than 1 comma under a non-string and non-char sequence
     */
    private String[] getFragmentsByType(String varLine, String type) throws ExtraCommasException{
        String[] varFragments = null;

        if (type.equals(INT) || type.equals(DOUBLE) || type.equals(BOOLEAN)){
            if (varLine.contains(",,") || varLine.contains(",;")) throw new ExtraCommasException(varLine);
            varFragments = varLine.replaceFirst("^\\s*", "")
                    .replaceAll("[,;=\\s]+", " ").split("\\s+");
        }
        else if (type.equals(CHAR) || type.equals(STRING)){
            varFragments = varLine.replaceFirst("^\\s*", "").split("=");

            varFragments[0] = varFragments[0].replaceAll("[,;\\s]", "");
            if (varFragments.length == 2) {
                varFragments[1] = varFragments[1].trim().replaceAll("[,;]+$", "");
            }
        }
        return varFragments;
    }

    /**
     * Verifies a declared variable has not been already declared in current scope
     * @param name variable name
     * @throws DoubleVariableDeclarationException if the variable was already declared at current scope
     */
    private void VerifyVariableNotInScope(String name) throws DoubleVariableDeclarationException{
        if (scopes.get(scopes.size() - 1).getVariablesMap().containsKey(name))
            throw new DoubleVariableDeclarationException(name);
    }

    /**
     * Checks assignment for an assignment line
     * @param line a line to check
     */
    private void checkAssignmentForLine(String line, boolean fromWithin)
            throws VariableException, BadLineException {
        Matcher variableMatcher = parser.getVariablesPattern().matcher(line);

        if(!variableMatcher.find()) throw new BadLineException(line);
        do{
            String[] varFragments = variableMatcher.group().replaceFirst("^\\s*", "").
                    replaceAll("[,;=\\s]+", " ").split("\\s+", 2);
            if(varFragments.length == 1) throw new BadLineException(line);

            validateAssignment(varFragments, getValueType(varFragments[0]), fromWithin);
        }
        while (variableMatcher.find());
    }


    /**
     * Checks assignment for one variable
     * @param varFragments assigned and assignee names
     * @param assignedType type of assigned variable
     * @return true if assignment succeeded, false otherwise
     * @throws FinalVariableException if tried to assign into a final variable
     * @throws TypeValueMismatchException if assignee type could not be assigned into assigned variable
     * @throws VariableNotFoundException if either the assigned or assignee variables wre not found
     */
    private boolean validateAssignment(String[] varFragments, String assignedType, boolean fromWithin)
            throws VariableException {
        String assigned = varFragments[0], assignee = varFragments[1];
        String assigneeType = getValueType(assignee);

        for (int i = scopes.size() - 1; i >= 0; --i){
            var scopeVariables = scopes.get(i).getVariablesMap();

            if (assignedType.equals("") && scopeVariables.containsKey(assigned)) //check first var in scope
                assignedType = scopeVariables.get(assigned).getType();

            if (assigneeType.equals("") && scopeVariables.containsKey(assignee)) { //check second var in scope
                var assigneeVariable = scopeVariables.get(assignee);
                assigneeType = assigneeVariable.getType();
                if(!assigneeVariable.getIsAssigned()) throw new UninitializedVariableUse(assigned, assignee);
            }

            if (!assignedType.equals("") && !assigneeType.equals("") &&
                    isAcceptedSubType(assignedType, assigneeType)){

                var assignedVariable = scopeVariables.get(assigned);
                if (assignedVariable != null && assignedVariable.getIsFinal())
                    throw new FinalVariableException(false);
                if (!fromWithin && assignedVariable != null) assignedVariable.assignVariable();
                return true;
            }
        }

        if(assignedType.equals("")) throw new VariableNotFoundException(assigned);
        throw new TypeValueMismatchException(varFragments);
    }

    /**
     * Checks type of the possible value
     * @param assignee the possible value as string
     * @return the value type, or "" if nothing fits
     */
    private String getValueType(String assignee){
        if(parser.getIntValuesPattern().matcher(assignee).matches()) return INT;
        else if(parser.getDoubleValuesPattern().matcher(assignee).matches()) return DOUBLE;
        else if(parser.getBooleanValuesPattern().matcher(assignee).matches()) return BOOLEAN;
        else if(parser.getStringValuesPattern().matcher(assignee).matches()) return STRING;
        else if(parser.getCharValuesPattern().matcher(assignee).matches()) return CHAR;
        else return ""; //Its a possible variable
    }

    /**
     * Is current assignee type fits to assigned type?
     * @param assignedType the type of the assigned variable
     * @param assigneeType the type of the assignee variable
     * @return true if fits, false otherwise
     */
    private boolean isAcceptedSubType(String assignedType, String assigneeType){
        switch (assignedType){
            case BOOLEAN: return assigneeType.equals(BOOLEAN) || assigneeType.equals(DOUBLE) ||
                        assigneeType.equals(INT);
            case DOUBLE: return assigneeType.equals(DOUBLE) || assigneeType.equals(INT);
            case INT: return assigneeType.equals(INT);
            case STRING: return assigneeType.equals(STRING);
            case CHAR: return assigneeType.equals(CHAR);
        }
        return false;
    }

    /**
     * Decides what to do in a given line in the global scope, based on parsing results
     * @param line the line as string
     * @param lineNumber the number of the line
     * @return the line number, to skip to next line
     * @throws SjavacException if there was any bad syntax or bad logic in any read line
     * @throws IOException if bufferedReader could not read line for some reason
     */
    private int globalScopeFactory(String line, int lineNumber) throws SjavacException, IOException{
        switch (parser.parseLineType(line)) {
            case EMPTY: break; //ignore empty line
            case COMMENT: break; //ignore comment line
            case VARIABLE: //validate the line logic, then save variables in scope
                extractVariables(line, false);
                break;
            case POSSIBLE_ASSIGN: //validate line logic
                checkAssignmentForLine(line, false);
                break;
            case FUNCTION: //validate line logic, then save function and move to end of function
                lineNumber = saveFunctionReference(line, lineNumber);
                break;
            default: throw new BadLineException(line);
        }
        return lineNumber;
    }

    /**
     * Decides what to do in a given line in an inner scope, based on parsing results
     * @param line the line as string
     * @param lineNumber the number of the line
     * @param lastReturnLine the line number of the last line that equaled 'return;'
     * @return lastReturnLine
     * @throws SjavacException if there was a problem with inner line logic or syntax
     */
    private int innerScopesFactory(String line, int lineNumber, int lastReturnLine) throws SjavacException {
        switch (parser.parseLineType(line)) {
            case IF_WHILE:
                scopes.add(new Scope());
                break;
            case VARIABLE:
                extractVariables(line, true);
                break;
            case FUNCTION_CALL:
                verifyFunctionCall(line);
                break;
            case RETURN:
                lastReturnLine = lineNumber;
                break;
            case END_OF_SCOPE:
                scopes.remove(scopes.size() - 1); //remove current scope
                if (scopes.size() != 1) break;
                //if last scope closed, check that previous line is 'return;'
                if (lastReturnLine == lineNumber - 1) return FUNCTION_END_CUE;
                else throw new NoReturnException(lineNumber - 1);
            case POSSIBLE_ASSIGN:
                checkAssignmentForLine(line, true);
                break;
            case FUNCTION: throw new InnerFunctionDeclarationException(lineNumber);
            case UNRECOGNIZED: throw new BadLineException(line);
        }
        return lastReturnLine;
    }
}