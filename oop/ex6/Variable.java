package oop.ex6;

import oop.ex6.Exceptions.VariableExceptions.NoVariableTypeException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Variable {
    private static final HashMap<String, String> assignValues = new HashMap<>();
    private final String type;
    private final String name;
    private final boolean isFinal;
    private final HashSet<String> assignTypes = new HashSet<>();
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
                assignTypes.add("boolean");
            case "double":
                assignTypes.add("double");
            case "int":
                assignTypes.add("int");
                break;
            case "String":
                assignTypes.add("String");
                break;
            case "char":
                assignTypes.add("char");
                break;
        }
    }

    //init regexes for allowed values to assign all types
    private static void initAllowedAssignValues() {
        String[] patterns = LineParser.getVariableRegexes();
        assignValues.put("int", patterns[0]);
        assignValues.put("double", patterns[1]);
        assignValues.put("boolean", patterns[2]);
        assignValues.put("String", patterns[3]);
        assignValues.put("char", patterns[4]);
    }


    /**
     * Assigns variable with a value
     * @param value value to the variable (as string)
     * @return true upon success, false otherwise
     */

    public boolean assign(String value) {
        return Pattern.compile(assignValues.get(this.type)).matcher(value).matches() && assignHelper();
    }

    /**
     * Assign a value from another value
     * @param variable the other variable to assign form
     * @return true upon success, false otherwise
     */
    public boolean assign(Variable variable) {
        return this.assignTypes.contains(variable.getType()) && variable.isAssigned() && assignHelper();
    }

    /**
     * Checks whether there's an attempt to assign a new value to a final value
     * @return true if assignment occurred, false otherwise
     */
    private boolean assignHelper(){
        if (isFinal && assigned) return false; //assignment to final
        return assigned = true;
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
