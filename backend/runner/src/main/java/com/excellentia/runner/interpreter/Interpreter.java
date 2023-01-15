package com.excellentia.runner.interpreter;

import com.excellentia.runner.process.ProcessResult;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Interpreter
{
    public String interpreterName;
    protected Process process;

    public Interpreter(String interpreterName)
    {
        this.interpreterName = interpreterName;
    }
    public ProcessResult execute(String exeFile) throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(interpreterName, exeFile);
        return new ProcessResult(process=processBuilder.start());
    }

    public ProcessResult execute(String exeFile,String inFile) throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(interpreterName, exeFile);
        processBuilder.redirectInput(new File(inFile));
        return new ProcessResult(process=processBuilder.start());
    }

    public void setTimeLimit(int timeLimitSeconds) throws InterruptedException
    {
        process.waitFor(timeLimitSeconds, TimeUnit.SECONDS);
    }
    public void setTimeLimitMilliSeconds(int timeLimit) throws InterruptedException
    {
        process.waitFor(timeLimit, TimeUnit.MILLISECONDS);
    }
}
