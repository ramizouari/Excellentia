package com.excellentia.backend.competitive;

import lombok.Getter;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;



@ResponseBody
public class SolutionAttempt
{
    public Integer runId;
    public Integer problemId;
    public String problemName;
}
