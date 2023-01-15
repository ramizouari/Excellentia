package com.excellentia.judge.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

@Getter
@Setter
@ResponseBody
public class SolutionVerdict
{
    private String verdict;
    private String message;
    public SolutionVerdict(Verdict verdict, String message)
    {
        this.verdict = verdict.toString();
        this.message = message;
    }
}
