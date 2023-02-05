//package oop.ex6;
//
//import oop.ex6.main.SjavacException;
//import oop.ex6.main.Verifier;
//import oop.ex6.utils.Scope;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//
//public class Tester {
//    private static final String s1 = "************************************************************\n" +
//            "\t%s\n************************************************************";
//    private static final String pre = "test_files/";
//    private static final String[] files = new String[]{
//            //invalidation tests:
//            "invalid/declaration/intInvalidDeclaration",
//            "invalid/declaration/doubleInvalidDeclaration",
//            "invalid/declaration/booleanInvalidDeclaration",
//            "invalid/declaration/StringInvalidDeclaration",
//            "invalid/declaration/charInvalidDeclaration",
//            "invalid/declaration/commentInvalidDeclaration",
//            "invalid/declaration/functionInvalidDeclaration",
//
//            "invalid/assignment/intInvalidAssignment--7",
//            "invalid/assignment/doubleInvalidAssignment--6",
//            "invalid/assignment/booleanInvalidAssignment--5",
//            "invalid/assignment/StringInvalidAssignment--7",
//            "invalid/assignment/charInvalidAssignment--7",
//
//            "invalid/generalInvalidLines--1",
//
//            //validation tests:
//            "valid/declaration/intValidDeclaration",
//            "valid/declaration/doubleValidDeclaration",
//            "valid/declaration/booleanValidDeclaration",
//            "valid/declaration/StringValidDeclaration",
//            "valid/declaration/charValidDeclaration",
//            "valid/declaration/commentValidDeclaration",
//
//            "valid/assignment/intValidAssignment",
//            "valid/assignment/doubleValidAssignment",
//            "valid/assignment/booleanValidAssignment",
//            "valid/assignment/StringValidAssignment",
//            "valid/assignment/charValidAssignment"
//    };
//
//    private static final String[] funcFiles = new String[]{
//            "invalid/functionInvalidCall",
//            "invalid/functionInvalid1",
//            "invalid/functionInvalid2",
//            "valid/functionValid"
//
//    };
//
//
//    public static void main(String[] args)
//    {
//        for(var file : files) mainHelper(file);
//        for (var file : funcFiles) mainHelper(file);
//    }
//
//    private static void mainHelper(String file){
//        System.out.println(String.format(s1, file));
//        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(pre + file)))
//        {
//            Verifier verifier = new Verifier();
//            verifier.testsFunctionHelper(file, bufferedReader);
//            verifier.testsFunction();
//        }
//        catch (IOException e) {System.err.println(e.getMessage());}
//    }
//
//    //add this to verifier class to test
//
////     private void printAllVariables(){ //TODO DELETE!
////        System.out.println("variables:");
////        for (var variable : scopes.get(0).getVariablesMap().entrySet()) System.out.println(variable);
////        System.out.println("\nfunctions: (num of params should be zero)");
////        functions.forEach((key, value) -> System.out.println(key + " : " + value));
////     }
////     public void testsFunctionHelper(String file, BufferedReader bufferedReader){ //todo DELETE
////        this.file = file;
////        this.bufferedReader = bufferedReader;
////        this.parser = new LineParser();
////     }
////    public void testsFunction() throws IOException{ //todo DELETE
////        /***********************GLOBAL SCOPE***********************/
////        int failedTests = 0;
////        scopes.add(new Scope());
////
////        String line;
////        int lineNum = 0;
////
////        while ((line = bufferedReader.readLine()) != null)
////            try{
////                lineNum = globalScopeFactory(line, lineNum) + 1;
////                System.out.println("Test passed in line " + lineNum);
////            }
////            catch (SjavacException e){
//////                System.err.println("in line: " + ++lineNum + ": " + e.getClass());
////                ++lineNum;
////                ++failedTests;
////            }
////        /***********************INNER SCOPES***********************/
////        var failedInnerLines = 0;
////        var innerLines = 0;
////        for (var entry : functions.entrySet()) {
////            scopes.add(new Scope()); //add function scope
////
////            bufferedReader = new BufferedReader(new FileReader("test_files/" + this.file));
////            for (int i = 0; i < entry.getValue().getStartLine(); ++i) {bufferedReader.readLine();}
////
////            String line2 = bufferedReader.readLine(); //get function declaration line
////            try{
////                addParamsToLocalScope(parser.getFunctionName(line2));  //add function parameters to it's scope
////            }
////            catch (SjavacException e){
//////                System.err.println(e.getMessage());
////            }
////
////            int lineNum2 = 1, lastReturnLineNumber = -1;
////            while ((line2 = bufferedReader.readLine()) != null) {
////                try{
////                    innerLines++;
////                    lastReturnLineNumber = innerScopesFactory(line2, lineNum2++, lastReturnLineNumber);
////                    if (lastReturnLineNumber == FUNCTION_END_CUE) break;
////                }
////                catch (SjavacException e){
////                    failedInnerLines++;
////                    System.err.println("INNER SCOPE FAIL in line: " + (lineNum2 + entry.getValue().getStartLine()) + ": " + e.getClass());
////                }
////
////            }
////        }
////        /*********************************************************/
////        System.out.println("***************************************\n\t\t\tTotal lines: " + lineNum +
////                "\n\t\t\tValid lines: " + (lineNum - failedTests) + "\n\t\t\tInvalid lines: " + failedTests +
////                "\nInner lines failed: " + failedInnerLines +
////                " from total of " + innerLines + "\n***************************************\n");
////    }
//}