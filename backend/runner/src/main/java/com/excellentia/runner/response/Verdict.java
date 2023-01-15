package com.excellentia.runner.response;

public enum Verdict
{
    ACCEPTED("Accepted"),
    WRONG_ANSWER("Wrong Answer"),
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded"),
    RUNTIME_ERROR("Runtime Error"),
    COMPILE_ERROR("Compile Error"),
    INTERNAL_ERROR("Internal Error");
    private String representation;
    Verdict(String representation)
    {
        this.representation = representation;
    }
    public String getRepresentation()
    {
        return representation;
    }
    public String toString()
    {
        return representation;
    }
}
