//package oop.ex6;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//
//public class TestLineParser {
//    public static void main(String[] args) throws IOException {
//        String file = args[0];
//        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//        LineParser parser = new LineParser();
//
//        //read the lines and prints whatever the parser thinks they are:
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            switch (parser.getLineType(line)) {
//                case WHILE -> System.out.println("while");
//                case COMMENT -> System.out.println("comment");
//                case UNRECOGNIZED -> System.out.println("unrecognized");
//                case RETURN -> System.out.println("return");
//                case FUNCTION -> System.out.println("function");
//                case VARIABLE -> System.out.println("variable");
//                case FUNCTION_CALL -> System.out.println("function call");
//                case IF -> System.out.println("if");
//                case END_OF_SCOPE -> System.out.println("end of scope");
//            }
//        }
//    }
//
//}
