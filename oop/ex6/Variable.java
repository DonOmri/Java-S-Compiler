package oop.ex6;

import oop.ex6.Exceptions.VariableExceptions.NoVariableTypeException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Variable {
    private static final String ALLOWED_INT_REGEX = "(\\-|\\+)?\\d+";
    private static final String ALLOWED_DOUBLE_REGEX = "((\\-|\\+)?\\d+.?\\d*?)|((\\-|\\+)?\\d*.?\\d+?)";
    private static final String ALLOWED_BOOLEAN_REGEX = "true|false|" + ALLOWED_INT_REGEX +
            ALLOWED_DOUBLE_REGEX;
    private static final String ALLOWED_STRING_REGEX = "\".*\"";
    private static final String ALLOWED_CHAR_REGEX = "\'[\\S ]\'";
    private static final HashMap<String, String> allowedAssignValues = new HashMap<>();
    private final String type;
    private final String name;
    private final boolean isFinal;
    private final HashSet<String> allowedAssignTypes = new HashSet<>();
    private boolean assigned;

    /**
     * Constructor
     * @param type type of variable to create
     * @param name name of variable to create
     * @param isFinal is the variable final
     * @throws NoVariableTypeException double verify of the variable type
     */
    public Variable(String type, String name, boolean isFinal) throws NoVariableTypeException {
        if (!LineParser.getVariableTypePattern().matcher(type).matches()) {
            throw new NoVariableTypeException(type);
        }
        this.isFinal = isFinal;
        this.type = type;
        this.name = name;
        initAllowedAssignTypes();
        initAllowedAssignValues();
    }

    //decides which assignment values are allowed, based on the type of the variable
    private void initAllowedAssignTypes() {
        switch (this.type) {
            case "boolean":
                allowedAssignTypes.add("boolean");
            case "double":
                allowedAssignTypes.add("double");
            case "int":
                allowedAssignTypes.add("int");
                break;
            case "String":
                allowedAssignTypes.add("String");
                break;
            case "char":
                allowedAssignTypes.add("char");
                break;
        }
    }

    //init regexes for allowed values to assign all types
    private static void initAllowedAssignValues() {
        allowedAssignValues.put("int", ALLOWED_INT_REGEX);
        allowedAssignValues.put("double", ALLOWED_DOUBLE_REGEX);
        allowedAssignValues.put("boolean", ALLOWED_BOOLEAN_REGEX);
        allowedAssignValues.put("String", ALLOWED_STRING_REGEX);
        allowedAssignValues.put("char", ALLOWED_CHAR_REGEX);
    }


    /**
     * Assigns variable with a value
     * @param value value to the variable (as string)
     * @return true if succeeded, false otherwise
     */

    //todo lots of unnecessary double-checking
    //todo should be throwing exceptions
    public boolean assign(String value) {
        //assignment to final that was already assigned
        if (!validAssignmentType(value)) return false; //wrong type-value assignment
        return assignHelper();
    }

    public boolean assign(Variable variable) {
        if (!validAssignmentType(variable)) return false; //wrong type-value assignment
        if (!variable.isAssigned()) return false;
        return assignHelper();
    }

    private boolean assignHelper(){
        if (isFinal && assigned) return false; //assignment to final
        return assigned = true;
    }


    //check that the type of value being assigned is valid
    private boolean validAssignmentType(String value) {
        return Pattern.compile(allowedAssignValues.get(this.type)).matcher(value).matches();
    }

    //check that the type of variable being assigned is valid
    private boolean validAssignmentType(Variable variable) {
        return this.allowedAssignTypes.contains(variable.getType());
    }

    /**
     * Getters for internal values
     */
    public String getType() {return this.type;}
    public String getName() {return name;}
    public boolean getIsFinal() {return isFinal;}
    public boolean isAssigned() {return assigned;}


    @Override
    public String toString() {
        return "Variable - type: " + type + ", name: " + name + (isFinal ? ", final" : ", not final");
    }
}
