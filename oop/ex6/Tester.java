package oop.ex6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Tester {
    private static final String s1 = "************************************************************\n" +
            "\t%s\n************************************************************";
    public static void main(String[] args)
    {
        var files = new String[]{
                //invalidation tests:
        "test_files/invalid/declaration/intInvalidDeclaration",
        "test_files/invalid/declaration/doubleInvalidDeclaration",
        "test_files/invalid/declaration/booleanInvalidDeclaration",
        "test_files/invalid/declaration/StringInvalidDeclaration",
        "test_files/invalid/declaration/charInvalidDeclaration",
        "test_files/invalid/declaration/commentInvalidDeclaration",
        "test_files/invalid/declaration/functionInvalidDeclaration",

        "test_files/invalid/assignment/intInvalidAssignment",
        "test_files/invalid/assignment/doubleInvalidAssignment",
        "test_files/invalid/assignment/booleanInvalidAssignment",
        "test_files/invalid/assignment/StringInvalidAssignment",
        "test_files/invalid/assignment/charInvalidAssignment",

        "test_files/invalid/functionInvalidCall",
        "test_files/invalid/generalInvalidLine",

                //validation tests:
        "test_files/valid/declaration/intValidDeclaration",
        "test_files/valid/declaration/doubleValidDeclaration",
        "test_files/valid/declaration/booleanValidDeclaration",
        "test_files/valid/declaration/StringValidDeclaration",
        "test_files/valid/declaration/charValidDeclaration",
        "test_files/valid/declaration/commentValidDeclaration",
        "test_files/valid/declaration/functionValidDeclaration",

        "test_files/valid/assignment/intValidAssignment",
        "test_files/valid/assignment/doubleValidAssignment",
        "test_files/valid/assignment/booleanValidAssignment",
        "test_files/valid/assignment/StringValidAssignment",
        "test_files/valid/assignment/charValidAssignment",

        "test_files/valid/functionValidCall",
        };

        for(var file : files){
            System.out.println(String.format(s1, file));
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file)))
            {
                Verifier verifier = new Verifier();
                verifier.testsFunctionHelper(file, bufferedReader);
                verifier.testsFunction();
            }
            catch (IOException e) {System.err.println(e.getMessage());}
        }


    }
}
