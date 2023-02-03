package oop.ex6.utils;

import java.util.HashMap;

/**
 * This class holds entire data for a certain scope
 */
public class Scope {
    private final HashMap<String, Variable> variablesMap = new HashMap<>();

    /**
     * Adds a variable to the scope
     * @param variable the variable to add
     * @return true upon adding successfully, false otherwise
     */
    public boolean addVariable(Variable variable) {
        var name = variable.getName();
        if (!variablesMap.containsKey(name)) {
            this.variablesMap.put(name, variable);
            return true;
        }
        return false;
    }

    /**
     * Getter for the variables map
     * @return the variables map
     */
    public HashMap<String, Variable> getVariablesMap(){
        return variablesMap;
    }

}
