package com.excellentia.backend.compiler;


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
}
