package oop.ex6.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Sjavac {
    private static final int SUCCESS_CODE = 0;
    private static final int JAVAC_FAIL_CODE = 1;
    private static final int IO_FAIL_CODE = 2;

    public static void main(String[] args)
    {
        Verifier verifier = new Verifier();

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0])))
        {
            verifier.verifySjavacFile(args[0], bufferedReader);
            System.out.println(SUCCESS_CODE);
        }
        catch (SjavacException e)
        {
            System.err.println(e.getMessage());
            System.out.println(JAVAC_FAIL_CODE);
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
            System.out.println(IO_FAIL_CODE);
        }
    }
}
