package com.excellentia.judge.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "discovery")
@Configuration
public class DiscoveryConfiguration
{
    private Host runner;
    private Host compiler;
}
