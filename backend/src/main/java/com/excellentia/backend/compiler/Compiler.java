package com.excellentia.backend.compiler;

import com.excellentia.backend.process.ProcessResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class Compiler
{
    List<String> flags;
    String compilerName;
    public Compiler(String compilerName)
    {
        this.compilerName = compilerName;
        flags=new ArrayList<>();
    }
    public void addFlag(String... flag)
    {
        flags.addAll(Arrays.stream(flag).toList());
    }
    public ProcessResult compile(String sourceCode, String exeFile) throws IOException
    {
        List<String> command = new ArrayList<String>();
        command.add(compilerName);
        command.addAll(flags);
        command.add(sourceCode);
        command.add("-o");
        command.add(exeFile);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        return new ProcessResult(processBuilder.start());
    }
}
