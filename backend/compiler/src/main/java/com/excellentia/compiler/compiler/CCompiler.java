package com.excellentia.compiler.compiler;

import com.excellentia.compiler.process.ProcessResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CCompiler extends Compiler
{
    public enum CompilerType
    {
        GCC("gcc"),
        CLANG("clang");

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
    public CCompiler(CPPCompiler.CompilerType compilerType)
    {
        super(compilerType.getCompilerName());
    }
    public CCompiler()
    {
        this(CPPCompiler.CompilerType.GCC);
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
