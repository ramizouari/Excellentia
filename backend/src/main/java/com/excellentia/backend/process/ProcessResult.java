package com.excellentia.backend.process;

import reactor.core.publisher.Mono;

import java.io.InputStream;

public class ProcessResult
{
    InputStream errorStream,outputStream;
    Mono<Integer> exitCode;
    public ProcessResult(InputStream errorStream, InputStream outputStream, int exitCode) {
        this.errorStream = errorStream;
        this.outputStream = outputStream;
        this.exitCode = Mono.just(exitCode);
    }
    public ProcessResult(Process process)
    {
        this.errorStream = process.getErrorStream();
        this.outputStream = process.getInputStream();
        this.exitCode = Mono.fromCallable(process::waitFor);
    }

    public void block()
    {
        exitCode.block();
    }

    public Mono<ProcessResultStatus> getStatus()
    {
        return exitCode.map(exitCode -> {
            if(exitCode == 0)
                return ProcessResultStatus.SUCCESS;
            else
                return ProcessResultStatus.ERROR;
        });
    }

    public Mono<Integer> getExitCode()
    {
        return exitCode;
    }

    public InputStream getErrorStream()
    {
        return errorStream;
    }

    public InputStream getOutputStream()
    {
        return outputStream;
    }
}
