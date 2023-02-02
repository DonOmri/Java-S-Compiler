package oop.ex6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Tester {
    private static final String s1 = "************************************************************\n" +
            "\t%s\n************************************************************";
    private static final String pre = "test_files/";
    private static final String[] files = new String[]{
            //invalidation tests:
            "invalid/declaration/intInvalidDeclaration",
            "invalid/declaration/doubleInvalidDeclaration",
            "invalid/declaration/booleanInvalidDeclaration",
            "invalid/declaration/StringInvalidDeclaration",
            "invalid/declaration/charInvalidDeclaration",
            "invalid/declaration/commentInvalidDeclaration",
            "invalid/declaration/functionInvalidDeclaration",

            "invalid/assignment/intInvalidAssignment",
            "invalid/assignment/doubleInvalidAssignment",
            "invalid/assignment/booleanInvalidAssignment",
            "invalid/assignment/StringInvalidAssignment",
            "invalid/assignment/charInvalidAssignment",

            "invalid/functionInvalidCall",
            "invalid/generalInvalidLines",

            //validation tests:
            "valid/declaration/intValidDeclaration",
            "valid/declaration/doubleValidDeclaration",
            "valid/declaration/booleanValidDeclaration",
            "valid/declaration/StringValidDeclaration",
            "valid/declaration/charValidDeclaration",
            "valid/declaration/commentValidDeclaration",

            "valid/assignment/intValidAssignment",
            "valid/assignment/doubleValidAssignment",
            "valid/assignment/booleanValidAssignment",
            "valid/assignment/StringValidAssignment",
            "valid/assignment/charValidAssignment",

            "valid/functionValid",
    };


    public static void main(String[] args)
    {
        for(var file : files){
            System.out.println(String.format(s1, file));
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(pre + file)))
            {
                Verifier verifier = new Verifier();
                verifier.testsFunctionHelper(file, bufferedReader);
                verifier.testsFunction();
            }
            catch (IOException e) {System.err.println(e.getMessage());}
        }


    }
}
