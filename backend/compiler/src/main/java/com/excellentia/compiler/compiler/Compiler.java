package com.excellentia.compiler.compiler;

import com.excellentia.compiler.process.ProcessResult;
import io.micrometer.core.annotation.Timed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    @Timed(value="compiler.time", description = "Time taken to compile a file")
    public abstract ProcessResult compile(String sourceCode, String exeFile) throws IOException;
}
