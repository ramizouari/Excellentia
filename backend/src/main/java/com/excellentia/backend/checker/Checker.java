package com.excellentia.backend.checker;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
}
