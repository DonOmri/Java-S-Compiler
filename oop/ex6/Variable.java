package oop.ex6;

import java.util.HashSet;

public class Variable {
    private final String type;
    private final boolean isFinal;
    private final String name;
    private final HashSet<String> allowedAssignTypes = new HashSet<>();
    private boolean assigned;

    //Constructor - called upon declaration of a variable
    public Variable(String type, String name, boolean isFinal) {
        this.isFinal = isFinal;
        this.type = type;
        this.name = name;
        initAllowedAssignTypes();
    }

    //decides which assignment values are allowed, based on the type of the variable
    private void initAllowedAssignTypes() {
        switch (this.type) {
            case "int":
                allowedAssignTypes.add("int");
                break;
            case "double":
                allowedAssignTypes.add("int");
                allowedAssignTypes.add("double");
                break;
            case "String":
                allowedAssignTypes.add("String");
                break;
            case "char":
                allowedAssignTypes.add("char");
                break;
            case "boolean":
                allowedAssignTypes.add("boolean");
                allowedAssignTypes.add("int");
                allowedAssignTypes.add("double");
                break;
        }
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

        //check the variable was assigned
        if (!variable.wasAssigned()) {
            return false;
        }

        //trying to assign with asn invalid type
        if (!validAssignmentType(variable)) {
            return false;
        }

        //everything's ok, so assign
        assigned = true;
        return true;

    }

    //TODO: implement
    //check that the type of value being assigned is valid
    private boolean validAssignmentType(String value) {
        return true;
    }

    //check that the type of variable being assigned is valid
    private boolean validAssignmentType(oop.ex6.Variable variable) {
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
