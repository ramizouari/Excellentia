package com.excellentia.compiler.compiler;

import com.excellentia.compiler.process.ProcessResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaCompiler extends Compiler
{
    public JavaCompiler()
    {
        super("javac");
    }

    @Override
    public ProcessResult compile(String sourcePath, String outputPath) throws IOException
    {
        List<String> command = new ArrayList<String>();
        command.add(compilerName);
        command.addAll(flags);
        command.add(sourcePath);
        command.add("-d");
        command.add(outputPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        return new ProcessResult(processBuilder.start());
    }
}
