package com.excellentia.judge.controllers;

import com.excellentia.judge.configuration.DiscoveryConfiguration;
import com.excellentia.judge.response.Verdict;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/judge")
public class JudgeController
{
    DiscoveryConfiguration discoveryConfiguration;
    public JudgeController(DiscoveryConfiguration discoveryConfiguration)
    {
        this.discoveryConfiguration=discoveryConfiguration;
    }
    @PostMapping("/submit")
    public Verdict submit(@RequestParam("file") MultipartFile file, @RequestBody String problemId)
    {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(discoveryConfiguration.getCompiler().getUrl(), file, Verdict.class);
    }

    @GetMapping("/status")
    public String judgeStatus()
    {
        return "Judge is up and running: "+ discoveryConfiguration.getCompiler().getHost();
    }
}
