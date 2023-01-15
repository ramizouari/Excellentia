package com.excellentia.backend.controllers;

import com.excellentia.backend.checker.ApproximateChecker;
import com.excellentia.backend.checker.Checker;
import com.excellentia.backend.compiler.CPPCompiler;
import com.excellentia.backend.configuration.DiskConfiguration;
import com.excellentia.backend.interpreter.SystemInterpreter;
import com.excellentia.backend.process.ProcessResult;
import com.excellentia.backend.response.ProcessOutput;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Path;

@RestController("solutionTester")
@RequestMapping("/api/competitive")
public class SolutionTester
{
    private final DiskConfiguration diskConfiguration;
    public SolutionTester(DiskConfiguration diskConfiguration)
    {
        this.diskConfiguration = diskConfiguration;
    }

    @PostMapping("/submit")
    public ProcessOutput processSolution(@RequestBody MultipartFile file,
                                         RedirectAttributes redirectAttributes)
    {
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        CPPCompiler compiler = new CPPCompiler();
        ProcessResult result=null;
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();
        String filePath = diskConfiguration.getRuns()+"/"+file.getOriginalFilename();
        String executablePath=filePath.substring(0,filePath.lastIndexOf('.'))+".exe";
        Integer exitCode=-1;
        try
        {
            file.transferTo(Path.of(filePath));
            SystemInterpreter interpreter = new SystemInterpreter();
            compiler.compile(filePath,executablePath).block();
            result=interpreter.execute(executablePath,diskConfiguration.getTests()+"/simulation/01.in");
            exitCode= result.getExitCode().block();

            var foundOutput=result.getOutputStream().readAllBytes();
            var expectedOutput=new FileInputStream(diskConfiguration.getTests()+"/simulation/01.ans").readAllBytes();
            Checker checker = new ApproximateChecker();
            if(checker.check(foundOutput,expectedOutput))
            {
                output.append("Correct Answer");
            }
            else
            {
                output.append("Wrong Answer");
            }
        }
        catch (IOException exception)
        {
            System.err.println(exception.getMessage());
        }

        return new ProcessOutput(output.toString(),error.toString(),exitCode);
    }
}
