package com.excellentia.backend.interpreter;

import com.excellentia.backend.process.ProcessResult;

import java.io.File;
import java.io.IOException;

public class Interpreter
{
    public String interpreterName;

    public Interpreter(String interpreterName)
    {
        this.interpreterName = interpreterName;
    }
    public ProcessResult execute(String exeFile) throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(interpreterName, exeFile);
        return new ProcessResult(processBuilder.start());
    }

    public ProcessResult execute(String exeFile,String inFile) throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(interpreterName, exeFile);
        processBuilder.redirectInput(new File(inFile));
        return new ProcessResult(processBuilder.start());
    }
}
