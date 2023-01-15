package com.excellentia.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController
{

    @GetMapping
    public String getMetrics()
    {
        return "Metrics";
    }
}
