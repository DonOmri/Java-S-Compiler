package oop.ex6;

import oop.ex6.main.Verifier;

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

            "invalid/assignment/intInvalidAssignment--7",
            "invalid/assignment/doubleInvalidAssignment--6",
            "invalid/assignment/booleanInvalidAssignment--5",
            "invalid/assignment/StringInvalidAssignment--7",
            "invalid/assignment/charInvalidAssignment--7",

            "invalid/generalInvalidLines--1",

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
    };

    private static final String[] funcFiles = new String[]{
//            "invalid/functionInvalidCall",
            "valid/functionValid",

    };


    public static void main(String[] args)
    {
//        for(var file : files) mainHelper(file);
        for (var file : funcFiles) mainHelper(file);
    }

    private static void mainHelper(String file){
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
