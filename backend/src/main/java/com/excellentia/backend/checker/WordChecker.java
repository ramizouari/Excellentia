package com.excellentia.backend.checker;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

public class WordChecker extends Checker
{

    private String augment(String s)
    {
        return " " + s + " ";
    }
    @Override
    public boolean check(byte[] A, byte[] B)
    {
        var U=new String(A).trim().split("\\s+");
        var V=new String(B).trim().split("\\s+");
        if(U.length!=V.length)
            return false;
        for(int i=0;i<U.length;i++)
            if(!U[i].equals(V[i]))
                return false;
        return true;
    }
}

