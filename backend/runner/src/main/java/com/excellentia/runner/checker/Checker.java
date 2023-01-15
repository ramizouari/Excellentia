package com.excellentia.runner.checker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Checker
{
    abstract public boolean check(byte[] A,byte[] B);

    boolean check(String A,String B)
    {
        return check(A.getBytes(),B.getBytes());
    }

    boolean check(Path A, Path B) throws IOException
    {
        return check(Files.readAllBytes(A),Files.readAllBytes(B));
    }

    public static Checker getInstance(CheckerType checkerType)
    {
        switch (checkerType)
        {
            case Integer:
                return new IntChecker();
            case Word:
                return new WordChecker();
            case Approximate:
                return new ApproximateChecker();
            default:
                return null;
        }
    }
}
