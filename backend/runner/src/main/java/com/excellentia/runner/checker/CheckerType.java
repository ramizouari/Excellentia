package com.excellentia.runner.checker;

public enum CheckerType
{
    Integer("integer"),
    Word("word"),
    Approximate("approximate");
    private String value;
    CheckerType(String value)
    {
        this.value = value;
    }
    public String toString()
    {
        return value;
    }
}
