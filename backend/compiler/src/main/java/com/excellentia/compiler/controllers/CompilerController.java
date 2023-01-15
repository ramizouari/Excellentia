package com.excellentia.compiler.controllers;

import com.excellentia.compiler.compiler.CPPCompiler;
import com.excellentia.compiler.configuration.DiskConfiguration;
import com.excellentia.compiler.dto.Run;
import com.excellentia.compiler.process.ProcessResult;
import com.excellentia.compiler.repositories.ProblemRepository;
import com.excellentia.compiler.repositories.RunRepository;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping

public class CompilerController
{
    private DiskConfiguration diskConfiguration;
    private ProblemRepository problemRepository;
    private RunRepository runRepository;
    public CompilerController(DiskConfiguration diskConfiguration,
                              ProblemRepository problemRepository,
                              RunRepository runRepository)
    {
        this.diskConfiguration = diskConfiguration;
        this.problemRepository = problemRepository;
        this.runRepository = runRepository;
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
                 RedirectAttributes redirectAttributes)
    {
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        CPPCompiler compiler = new CPPCompiler();
        Run newRun=new Run();
        try
        {
            if(problemId == null)
                problemId=problemRepository.findByName(problemName).get().getId();
            newRun.setProblemId(problemRepository.findById(problemId).get().getId());
            newRun.setExecutedAt(new java.sql.Time(System.currentTimeMillis()));
            newRun.setCompiler(compilerName);
            newRun=runRepository.save(newRun);
            var filePath = diskConfiguration.getRunsPath()
                    .resolve(newRun.getId().toString().concat(".cpp"));
            file.transferTo(filePath);

            ProcessResult result = compiler.compile(filePath.toString(), filePath.toString().replace(".cpp",".exe"));
            var errorStream=new DataInputStream(result.getErrorStream());
            StringBuilder error=new StringBuilder();
            String line;
            while((line=errorStream.readLine())!=null)
            {
                error.append(line);
            }
            System.err.println(error);
            result.block();
            if(result.getExitCode().block()!=0)
                newRun.setVerdict("Compilation Error");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return newRun;
    }
}
