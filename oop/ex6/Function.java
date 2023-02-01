package oop.ex6;

import java.util.ArrayList;

public class Function {
    private final int startLine;
    private final ArrayList<Variable> parameters = new ArrayList<>();

    public Function(int startLine){
        this.startLine = startLine;
    }

    //TODO: complete this check
    public boolean validCall(String[] params){
        // function call - method name exists, params should be - good types, initialized, same number
        // get parameters sent
        return true;
    }

    public ArrayList<Variable> getFunctionParameters() {return parameters;}
    public int getStartLine() {return startLine;}

    @Override
    public String toString() {
        return "startLine = " + startLine + ", num of params = " + parameters.size();
    }
}
