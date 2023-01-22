package oop.ex6;

import oop.ex6.main.Sjavac;

public class OurTests {

    public static String[] valid_files = new String[]{
            "valid1", "valid2", "valid3", "valid4", "valid5", "valid6", "valid7", "valid8", "valid9",
            "valid10"};
    public static String[] invalid_files = new String[]{
            "invalid1", "invalid2", "invalid3", "invalid4", "invalid5", "invalid6", "invalid7", "invalid8",
            "invalid9", "invalid10"};

    public static void main(String[] args) {

        System.out.println("************* 10 valid files - check results are 0 *************");
        for (String file : valid_files) {
            Sjavac.main(new String[]{"our_tests/" + file + ".txt"});
        }
        System.out.println("******************* end *****************\n\n");

        System.out.println("************* 10 invalid files check results are 1 *************");
        for (String file : invalid_files) {
            Sjavac.main(new String[]{"our_tests/" + file + ".txt"});
        }
        System.out.println("******************* end *****************");

    }

}
