package oop.ex6.main;

import oop.ex6.Verifier;
import java.io.IOException;

public class Sjavac {

    public static void main(String[] args)
    {
        Verifier verifier = new Verifier();

        try
        {
            verifier.verifySjavacFile(args[0]);
            System.out.println(0);
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
            System.out.println(2);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            System.out.println(1);
        }
    }
}
