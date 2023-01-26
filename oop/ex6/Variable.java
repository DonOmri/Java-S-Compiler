package oop.ex6;

import oop.ex6.Exceptions.NoVariableTypeException;
import oop.ex6.Exceptions.VariableException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Variable {
    private static final HashMap<String, String> allowedAssignValues = new HashMap<>();
    private final String type;
    private final boolean isFinal;
    private final String name;
    private final HashSet<String> allowedAssignTypes = new HashSet<>();
    private boolean assigned;

    //Constructor - called upon declaration of a variable
    public Variable(String type, String name, boolean isFinal) throws NoVariableTypeException {
        if (!Pattern.compile("char|int|String|double|boolean").matcher(type).matches()) {
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
    private void initAllowedAssignValues() {

        String allowed_for_int_regex = "(\\-|\\+)?\\d+";
        String allowed_for_double_regex = "((\\-|\\+)?\\d+.?\\d*?)|((\\-|\\+)?\\d*.?\\d+?)";
        String allowed_for_boolean_regex = "true|false|" + allowed_for_int_regex + allowed_for_double_regex;
        String allowed_for_string_regex = "\".*\"";
        String allowed_for_char_regex = "\'[\\S ]\'";

        allowedAssignValues.put("int", allowed_for_int_regex);
        allowedAssignValues.put("String", allowed_for_string_regex);
        allowedAssignValues.put("char", allowed_for_char_regex);
        allowedAssignValues.put("boolean", allowed_for_boolean_regex);
        allowedAssignValues.put("double", allowed_for_double_regex);

    }

    //assign with a value
    public boolean assign(String value) {
        //assignment to final that was already assigned
        if (isFinal && assigned) {
            return false;
        }

        //trying to assign with asn invalid type
        if (!validAssignmentType(value)) {
            return false;
        }

        //everything's ok, so assign
        assigned = true;
        return true;

    }

    //assign with a variable
    public boolean assign(Variable variable) {
        //assignment to final that was already assigned
        if (isFinal && assigned) {
            return false;
        }

        //assert that the variable was assigned
        //TODO: uncomment when treating variable assignment is working
//        if (!variable.wasAssigned()) {
//            return false;
//        }

        //trying to assign with asn invalid type
        if (!validAssignmentType(variable)) {
            return false;
        }

        //everything's ok, so assign
        assigned = true;
        return true;

    }

    //check that the type of value being assigned is valid
    private boolean validAssignmentType(String value) {
        return Pattern.compile(allowedAssignValues.get(this.type)).matcher(value).matches();
    }

    //check that the type of variable being assigned is valid
    private boolean validAssignmentType(Variable variable) {
        return this.allowedAssignTypes.contains(variable.getType());
    }

    //getter for type
    public String getType() {
        return this.type;
    }

    public String getName() {
        return name;
    }

    //getter for assigned
    public boolean wasAssigned() {
        return this.assigned;
    }

    @Override
    public String toString() {
        return "Variable - type: " + type + ", name: " + name + (isFinal ? ", final" : ", not final");
    }
}
