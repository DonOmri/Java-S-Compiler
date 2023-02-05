package oop.ex6.utils;

import java.util.ArrayList;

/**
 * This class holds data for a single function
 */
public class Function {
    private final int startLine;
    private final ArrayList<Variable> parameters = new ArrayList<>();

    /**
     * Constructor
     * @param startLine starting line of the function
     */
    public Function(int startLine){
        this.startLine = startLine;
    }

    /**
     * Getters for internal variables
     */
    public ArrayList<Variable> getFunctionParameters() {return parameters;}
    public int getStartLine() {return startLine;}

    @Override
    public String toString() {
        return "startLine = " + startLine + ", num of params = " + parameters.size();
    }
}
