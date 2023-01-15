package com.excellentia.compiler.compiler;


import com.excellentia.compiler.process.ProcessResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CPPCompiler extends Compiler
{

    public enum CompilerType
    {
        GCC("g++"),
        CLANG("clang++");

        private String compilerName;
        CompilerType(String name)
        {
            this.compilerName=name;
        }
        public String getCompilerName()
        {
            return compilerName;
        }
    }

    public CPPCompiler(CompilerType compilerType)
    {
        super(compilerType.getCompilerName());
    }
    public CPPCompiler()
    {
        this(CompilerType.GCC);
    }

    @Override
    public ProcessResult compile(String sourcePath, String outputPath) throws IOException
    {
        List<String> command = new ArrayList<String>();
        command.add(compilerName);
        command.addAll(flags);
        command.add(sourcePath);
        command.add("-o");
        command.add(outputPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        return new ProcessResult(processBuilder.start());
    }
}
