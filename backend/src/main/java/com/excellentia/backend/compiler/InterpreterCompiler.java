package com.excellentia.backend.compiler;

import com.excellentia.backend.process.ProcessResult;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

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
