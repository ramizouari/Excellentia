package com.excellentia.compiler.dto;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name="Problem")
public class Problem
{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name ="time_limit")
    private Long timeLimit;
    private String description;
}
