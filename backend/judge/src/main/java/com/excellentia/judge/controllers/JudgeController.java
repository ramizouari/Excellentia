package com.excellentia.judge.controllers;

import com.excellentia.judge.configuration.DiscoveryConfiguration;
import com.excellentia.judge.response.Run;
import com.excellentia.judge.response.SolutionVerdict;
import com.excellentia.judge.response.Verdict;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/judge")
public class JudgeController
{
    DiscoveryConfiguration discoveryConfiguration;
    Logger logger = LoggerFactory.getLogger(JudgeController.class);

    public JudgeController(DiscoveryConfiguration discoveryConfiguration)
    {
        this.discoveryConfiguration=discoveryConfiguration;
    }
    @GetMapping("/{problemId}")
    public List<String> getRuns(@PathVariable String problemId)
    {
        return null;
    }
    @PostMapping("/{problemId}")
    public Map submit(@RequestParam("file") MultipartFile file, @PathVariable String problemId,
                      HttpServletRequest request) throws IOException
    {
        Random random=new Random();
        String requestId=String.valueOf(random.nextInt());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        logger.atInfo().setMessage("Receiving submission at {} for problem {}, {} is set to {}.")
                .addArgument(formatter.format(date))
                .addArgument(problemId)
                .addArgument("request_id")
                .addArgument(requestId)
                .addKeyValue("ip_client",request.getRemoteAddr())
                .log();


        logger.atInfo().setMessage("File name is {}.")
                .addArgument(file.getOriginalFilename())
                .addKeyValue("ip_client",request.getRemoteAddr())
                .addKeyValue("request_id",requestId)
                .log();
        MultiValueMap<String, Object> parts =
                new LinkedMultiValueMap<String, Object>();

        logger.atDebug().setMessage("Preparing to send file to compiler")
                .addKeyValue("ip_client",request.getRemoteAddr())
                .addKeyValue("request_id",requestId)
                .addKeyValue("file_name",file.getOriginalFilename())
                .addKeyValue("file_size",file.getSize())
                .addKeyValue("compiler_url",discoveryConfiguration.getCompiler().getUrl())
                .log();

        parts.add("file", file.getResource());
        parts.add("filename", file.getOriginalFilename());
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-ID",requestId);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        var compileRequestEntity = new HttpEntity<>(parts, headers);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        RestTemplate compileRequest = new RestTemplate();
        compileRequest.getMessageConverters().add(converter);

        logger.atDebug().setMessage("Sending submission file to compiler.")
                .addKeyValue("ip_client",request.getRemoteAddr())
                .addKeyValue("request_id",requestId)
                .addKeyValue("file_name",file.getOriginalFilename())
                .addKeyValue("file_size",file.getSize())
                .addKeyValue("compiler_url",discoveryConfiguration.getCompiler().getUrl())
                .log();

        var run=compileRequest.postForObject(discoveryConfiguration.getCompiler().getUrl().concat("/compile?problem_id=%s&compiler=gcc".formatted(problemId)), compileRequestEntity, Map.class);


        logger.atDebug().setMessage("Received response {}").addArgument(run)
                .addKeyValue("ip_client",request.getRemoteAddr())
                .addKeyValue("request_id",requestId)
                .addKeyValue("file_name",file.getOriginalFilename())
                .addKeyValue("file_size",file.getSize())
                .addKeyValue("compiler_url",discoveryConfiguration.getCompiler().getUrl())
                .log();

        logger.atDebug().setMessage("Preparing to send request to runner")
                .addKeyValue("ip_client",request.getRemoteAddr())
                .addKeyValue("request_id",requestId)
                .addKeyValue("file_name",file.getOriginalFilename())
                .addKeyValue("file_size",file.getSize())
                .addKeyValue("runner_url",discoveryConfiguration.getRunner().getUrl())
                .log();

        RestTemplate runRequest = new RestTemplate();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var runRequestEntity = new HttpEntity<>(run, headers);

        run.put("runId",run.get("id"));
        run.put("testId",1);
        run.put("timeLimit",1);

        logger.atDebug().setMessage("Sending submission file to runner")
                .addKeyValue("ip_client",request.getRemoteAddr())
                .addKeyValue("request_id",requestId)
                .addKeyValue("file_name",file.getOriginalFilename())
                .addKeyValue("file_size",file.getSize())
                .addKeyValue("runner_url",discoveryConfiguration.getRunner().getUrl())
                .addKeyValue("run_id",run.get("id"))
                .addKeyValue("test_id",1)
                .addKeyValue("time_limit",1)
                .addKeyValue("problem_id",problemId)
                .log();

        var response= runRequest.postForObject(discoveryConfiguration.getRunner().getUrl().concat("/runner"), runRequestEntity, Map.class);
        logger.atDebug().setMessage("Received response {}.")
                .addArgument(response)
                .addKeyValue("ip_client",request.getRemoteAddr())
                .addKeyValue("request_id",requestId)
                .addKeyValue("file_name",file.getOriginalFilename())
                .addKeyValue("file_size",file.getSize())
                .addKeyValue("runner_url",discoveryConfiguration.getRunner().getUrl())
                .addKeyValue("run_id",run.get("id"))
                .addKeyValue("test_id",1)
                .addKeyValue("time_limit",1)
                .addKeyValue("problem_id",problemId)
                .log();
        return response;
    }

    @GetMapping("/status")
    public String judgeStatus()
    {
        return "Judge is up and running: "+ discoveryConfiguration.getCompiler().getHost();
    }
}
