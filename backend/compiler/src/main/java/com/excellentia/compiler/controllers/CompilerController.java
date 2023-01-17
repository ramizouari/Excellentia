package com.excellentia.compiler.controllers;

import com.excellentia.compiler.compiler.CPPCompiler;
import com.excellentia.compiler.configuration.DiskConfiguration;
import com.excellentia.compiler.dto.Run;
import com.excellentia.compiler.process.ProcessResult;
import com.excellentia.compiler.repositories.ProblemRepository;
import com.excellentia.compiler.repositories.RunRepository;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping

public class CompilerController
{
    private DiskConfiguration diskConfiguration;
    private ProblemRepository problemRepository;
    private RunRepository runRepository;
    private final MeterRegistry registry;


    Logger logger = LoggerFactory.getLogger(CompilerController.class);
    public CompilerController(DiskConfiguration diskConfiguration,
                              ProblemRepository problemRepository,
                              RunRepository runRepository,
                              MeterRegistry registry)
    {
        this.diskConfiguration = diskConfiguration;
        this.problemRepository = problemRepository;
        this.runRepository = runRepository;
        this.registry=registry;
    }
    @GetMapping("/compilers")
    List<String> getCompilers()
    {
        return List.of("gcc","clang","g++","clang++","javac");
    }

    @PostMapping("/compile")
    Run compile(@RequestParam("compiler") String compilerName,
                @Nullable @RequestParam("problem_id") Long problemId,
                @Nullable @RequestParam("problem_name") String problemName,
                @RequestBody MultipartFile file,
                RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response)
    {
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        logger.atInfo().log("You successfully uploaded " + file.getOriginalFilename() + "!");

        String requestId = request.getHeader("X-Request-ID");
        if(requestId==null)
            logger.atWarn().log("Request ID is null");


        CPPCompiler compiler = new CPPCompiler();
        Run newRun=new Run();
        Path filePath;
        try
        {
            if (problemId == null)
                problemId = problemRepository.findByName(problemName).orElseThrow(NoSuchElementException::new).getId();
            registry.counter("problem_submissions","compiler",compilerName,"problem_id",problemId.toString()).increment();
            newRun.setProblemId(problemRepository.findById(problemId).orElseThrow(NoSuchElementException::new).getId());
            newRun.setExecutedAt(new java.sql.Time(System.currentTimeMillis()));
            newRun.setCompiler(compilerName);
            logger.atInfo()
                    .setMessage("Creating new run for problem id: {}")
                    .addArgument(problemId)
                    .addKeyValue("request_id",requestId)
                    .addKeyValue("compiler",compilerName)
                    .log();
            newRun = runRepository.save(newRun);
            logger.atInfo()
                            .setMessage("Created new run with id: {}")
                            .addArgument(newRun.getId())
                            .addKeyValue("request_id",requestId)
                            .addKeyValue("compiler",compilerName)
                            .log();
            logger.atDebug()
                    .setMessage("Saving source code : {}")
                    .addArgument(newRun.getId())
                    .addKeyValue("request_id",requestId)
                    .addKeyValue("compiler",compilerName)
                    .log();
            filePath = diskConfiguration.getRunsPath()
                    .resolve(newRun.getId().toString().concat(".cpp"));
            file.transferTo(filePath);
        }
        catch(NoSuchElementException e)
        {
            logger.atWarn()
                    .setMessage("Problem with id: {} not found")
                    .addArgument(problemId)
                    .addKeyValue("request_id",requestId)
                    .addKeyValue("compiler",compilerName)
                    .log();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        catch (IOException e)
        {
            logger.atError()
                    .setMessage("Error while saving source code")
                    .addArgument(problemId)
                    .addKeyValue("request_id",requestId)
                    .addKeyValue("compiler",compilerName)
                    .log();
            registry.counter("code_submissions","compiler",compilerName,"problem_id",problemId.toString(),"state","internal_error").increment();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }

        try
        {
            ProcessResult result = compiler.compile(filePath.toString(), filePath.toString().replace(".cpp",".exe"));
            var errorStream=new DataInputStream(result.getErrorStream());
            StringBuilder error=new StringBuilder();
            String line;
            while((line=errorStream.readLine())!=null)
                error.append(line);
            System.err.println(error);
            result.block();
            if(result.getExitCode().block()!=0) {
                newRun.setVerdict("Compilation Error");
                registry.counter("code_submissions","compiler",compilerName,"problem_id",problemId.toString(),"state","compilation_error").increment();
            }
            else
            {
                registry.counter("code_submissions","compiler",compilerName,"problem_id",problemId.toString(),"state","compiled").increment();
            }
        }
        catch (IOException e)
        {
            logger.atError()
                    .setMessage("Error while compiling source code")
                    .addArgument(problemId)
                    .addKeyValue("request_id",requestId)
                    .addKeyValue("compiler",compilerName)
                    .log();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        return newRun;
    }
}
