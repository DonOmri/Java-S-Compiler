package oop.ex6;

import oop.ex6.main.Sjavac;

import java.util.HashMap;

public class OurTests {

    public static String[] valid_files = new String[]{"valid3"};
    public static String[] invalid_files = new String[]{};

    public static void testValueClassification(String[] values, String type) throws Exception {
        for (String value : values) {
            if (!new Variable(type, "", false).assign(value)) {
                System.out.println("[ FAILED ]  thought " + value + " is an invalid " + type);
            }
        }
    }

    public static void testEverything() {
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

    public static void main(String[] args) {
        //test the classification of values
        HashMap<String, String[]> type_valid_values = new HashMap<>();
        type_valid_values.put("int", new String[]{"17", "-3", "+001"});
        type_valid_values.put("char", new String[]{"\'a\'", "\'@\'"});
        type_valid_values.put("boolean", new String[]{"true", "false", "5.2", "-1"});
        type_valid_values.put("double", new String[]{"5.21", "2.", ".1"});
        type_valid_values.put("String", new String[]{"\"hi\"", "\"i#\""});
        type_valid_values.forEach((key, value) -> {
            try {
                testValueClassification(value, key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // test all our test cases
        testEverything();
    }

}
