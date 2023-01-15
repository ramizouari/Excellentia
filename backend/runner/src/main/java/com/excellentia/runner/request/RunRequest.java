package com.excellentia.runner.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunRequest
{
    private Long problemId;
    private Long runId;

    private Long testId;
    private Integer timeLimit;

}
