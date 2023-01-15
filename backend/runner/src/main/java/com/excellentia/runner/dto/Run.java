package com.excellentia.runner.dto;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@Entity(name="Run")
public class Run
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name ="problem_id")
    private Long problemId;
    private Time executedAt;
    private Integer time;
    private Integer memory;
    private String verdict="In Queue";
    private String compiler;
}
