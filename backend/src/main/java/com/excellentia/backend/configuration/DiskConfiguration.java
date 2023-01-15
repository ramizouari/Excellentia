package com.excellentia.backend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "disk")
@Configuration
public class DiskConfiguration
{
    private String runs;
    private String tests;

    public String getRuns() {
        return runs;
    }
    public void setRuns(String runs) {
         this.runs = runs;
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }
}
