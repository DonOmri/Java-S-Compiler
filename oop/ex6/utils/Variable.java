package oop.ex6.utils;

import oop.ex6.main.LineParser;
import oop.ex6.main.VariableExceptions.NoVariableTypeException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * This class holds data for a single variable
 */
public class Variable {
    private static final HashMap<String, String> assignValues = new HashMap<>();
    private final String type;
    private final String name;
    private final HashSet<String> assignTypes = new HashSet<>();
    private final boolean isFinal;
    private boolean isAssigned;

    /**
     * Constructor
     * @param type type of variable to create
     * @param name name of variable to create
     * @param isFinal is the variable final
     * @param isAssigned whether the variable is already assigned or not
     * @throws NoVariableTypeException double verify of the variable type
     */
    public Variable(String type, String name, boolean isFinal, boolean isAssigned)
            throws NoVariableTypeException {
        if (!LineParser.getVariableTypePattern().matcher(type).matches()) {
            throw new NoVariableTypeException(type);
        }
        this.isFinal = isFinal;
        this.type = type;
        this.name = name;
        this.isAssigned = isAssigned;
        initAllowedAssignTypes();
        initAllowedAssignValues();
    }

    /**
     * Decides which assignment values are allowed, based on the type of the variable
     */
    private void initAllowedAssignTypes() {
        assignTypes.add(type);
        if (type.equals("double") || type.equals("boolean")){
            assignTypes.add("int");
            assignTypes.add("double");
        }
    }

    /**
     * Initializes regexes for allowed values to assign all types
     */
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
        return this.assignTypes.contains(variable.getType()) && variable.getIsAssigned() && assignHelper();
    }

    /**
     * Checks whether there's an attempt to assign a new value to a final value
     * @return true if assignment occurred, false otherwise
     */
    private boolean assignHelper(){
        if (isFinal && isAssigned) return false; //assignment to final
        return isAssigned = true;
    }

    /**
     * Getters for internal values
     */
    public String getType() {return this.type;}
    public String getName() {return name;}
    public boolean getIsFinal() {return isFinal;}
    public boolean getIsAssigned() {return isAssigned;}

    /**
     * Setter for isAssigned field
     */
    public void assignVariable(){
        isAssigned = true;
    }

    @Override
    public String toString() {
        return "Variable\ntype: " + type + "\nname: " + name + "\nfinal: " + isFinal + "\nassigned: " +
                isAssigned + "\n";
    }
}
