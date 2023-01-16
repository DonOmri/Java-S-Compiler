package oop.ex6;

import java.util.HashMap;

//holds the relevant data for the scope - all variables of the scope
public class Scope {
    public final HashMap<String, Variable> variablesMap = new HashMap<>();

    public boolean addVariable(Variable variable) {
        var name = variable.getName();
        if (!variablesMap.containsKey(name)) {
            this.variablesMap.put(name, variable);
            return true;
        }
        return false;
    }

    public Variable getVariable(String name) {
        if (variablesMap.containsKey(name)) {
            return this.variablesMap.get(name);
        }
        return null;
    }

}
