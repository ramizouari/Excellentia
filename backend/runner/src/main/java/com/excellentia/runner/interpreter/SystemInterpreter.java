package com.excellentia.runner.interpreter;

import com.excellentia.runner.process.ProcessResult;

import java.io.File;
import java.io.IOException;

public class SystemInterpreter extends Interpreter
{
    public SystemInterpreter()
    {
        super("true");
    }

    @Override
    public ProcessResult execute(String exeFile) throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(exeFile);
        return new ProcessResult(process=processBuilder.start());
    }

    @Override
    public ProcessResult execute(String exeFile, String inFile) throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(exeFile);
        processBuilder.redirectInput(new File(inFile));
        return new ProcessResult(process=processBuilder.start());
    }
}
