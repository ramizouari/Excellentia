package com.excellentia.compiler.compiler;

import com.excellentia.compiler.process.ProcessResult;

import java.io.ByteArrayInputStream;

public class InterpreterCompiler extends Compiler
{
    public InterpreterCompiler(String compilerName) {
        super(compilerName);
    }
    public InterpreterCompiler() {
        super("true");
    }

    @Override
    public ProcessResult compile(String sourcePath, String outputPath) {

        var output = new ByteArrayInputStream("".getBytes());
        var error = new ByteArrayInputStream("".getBytes());
        return new ProcessResult(output, error, 0);
    }
}
