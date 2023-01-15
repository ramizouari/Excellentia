package com.excellentia.runner.checker;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class ApproximateChecker extends Checker
{
    double epsilon;

    public ApproximateChecker()
    {
        this(1e-6);
    }
    public ApproximateChecker(double epsilon)
    {
        this.epsilon = epsilon;
    }

    @Override
    public boolean check(byte[] A,byte[] B)
    {
        var S1 = new Scanner(new ByteArrayInputStream(A));
        var S2 = new Scanner(new ByteArrayInputStream(B));
        while(S1.hasNextDouble() && S2.hasNextDouble())
        {
            double a = S1.nextDouble();
            double b = S2.nextDouble();
            if(Math.abs(a-b)>epsilon)
                return false;
        }
        return !(S1.hasNext() || S2.hasNext());
    }
}
