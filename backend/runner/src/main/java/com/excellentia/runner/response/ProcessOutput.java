package com.excellentia.runner.response;

import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public class ProcessOutput
{
    private String output, error;
    private int exitCode;
    public ProcessOutput(String output, String error, int exitCode)
    {
        this.output = output;
        this.error = error;
        this.exitCode = exitCode;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getExitCode() {
        return exitCode;
    }
}
