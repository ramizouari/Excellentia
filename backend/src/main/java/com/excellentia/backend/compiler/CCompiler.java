package com.excellentia.backend.compiler;

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
}
