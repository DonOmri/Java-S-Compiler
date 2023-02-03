package oop.ex6.utils;

import java.util.ArrayList;

public class Function {
    private final int startLine;
    private final ArrayList<Variable> parameters = new ArrayList<>();

    public Function(int startLine){
        this.startLine = startLine;
    }

    public ArrayList<Variable> getFunctionParameters() {return parameters;}
    public int getStartLine() {return startLine;}

    @Override
    public String toString() {
        return "startLine = " + startLine + ", num of params = " + parameters.size();
    }
}
