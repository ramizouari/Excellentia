package com.excellentia.runner.checker;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class IntChecker extends Checker
{
    @Override
    public boolean check(byte[] A, byte[] B)
    {
        var S1 = new Scanner(new ByteArrayInputStream(A));
        var S2 = new Scanner(new ByteArrayInputStream(B));
        while(S1.hasNextLong() && S2.hasNextLong())
        {
            long a = S1.nextLong();
            long b = S2.nextLong();
            if(a!=b)
                return false;
        }
        return !(S1.hasNext() || S2.hasNext());
    }
}

