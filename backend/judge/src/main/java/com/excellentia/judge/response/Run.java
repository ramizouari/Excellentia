package com.excellentia.judge.response;


import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
public class Run
{


    private Long id;
    private Long problemId;
    private Time executedAt;
    private Integer time;
    private Integer memory;
    private String verdict="In Queue";
    private String compiler;
}
