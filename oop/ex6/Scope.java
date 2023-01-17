package oop.ex6;

import java.util.HashMap;


/**
 * This class holds entire data for a certain scope
 */
public class Scope {
    public final HashMap<String, Variable> variablesMap = new HashMap<>();

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
     * Gets a variable from the scope
     * @param name name of the variable
     * @return the variable itself upon success, null otherwise
     */
    public Variable getVariable(String name) {
        if (variablesMap.containsKey(name)) {
            return this.variablesMap.get(name);
        }
        return null;
    }

}
