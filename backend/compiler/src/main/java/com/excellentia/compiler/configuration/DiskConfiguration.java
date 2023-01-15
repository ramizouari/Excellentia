package com.excellentia.compiler.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties(prefix = "disk")
@Configuration
public class DiskConfiguration
{
    private String runs;
    private String tests;

    public String getRuns() {
        return runs;
    }
    public void setRuns(String mount) {
         this.runs = mount;
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }

    public Path getRunsPath()
    {
        return Paths.get(runs);
    }

    public Path getTestsPath()
    {
        return Paths.get(tests);
    }

    public String getTestName(Integer testId)
    {
        return "%03d".formatted(testId);
    }

    public String getInputFileName(Integer testId)
    {
        return getTestName(testId).concat(".in");
    }

    public String getOutputFileName(Integer testId)
    {
        return getTestName(testId).concat(".ans");
    }
}
