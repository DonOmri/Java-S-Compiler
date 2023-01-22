package oop.ex6;

import oop.ex6.main.Sjavac;

public class OurTests {

    public static String[] valid_files = new String[]{};
    public static String[] invalid_files = new String[]{"invalid1"};

    public static void main(String[] args) {

        System.out.println("************* 10 valid files - check all results are 0 *************");
        for (String file : valid_files) {
            Sjavac.main(new String[]{"our_tests/" + file + ".txt"});
        }
        System.out.println("******************* end *****************\n\n");

        System.out.println("************* 10 invalid files check all results are 1 *************");
        for (String file : invalid_files) {
            Sjavac.main(new String[]{"our_tests/" + file + ".txt"});
        }
        System.out.println("******************* end *****************");

    }

}
