package com.excellentia.runner.controllers;

import com.excellentia.runner.checker.ApproximateChecker;
import com.excellentia.runner.checker.Checker;
import com.excellentia.runner.checker.CheckerType;
import com.excellentia.runner.checker.WordChecker;
import com.excellentia.runner.configuration.DiskConfiguration;
import com.excellentia.runner.interpreter.SystemInterpreter;
import com.excellentia.runner.process.ProcessResult;
import com.excellentia.runner.repositories.ProblemRepository;
import com.excellentia.runner.repositories.RunRepository;
import com.excellentia.runner.request.RunRequest;
import com.excellentia.runner.response.ProcessOutput;
import com.excellentia.runner.response.SolutionVerdict;
import com.excellentia.runner.response.Verdict;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@RestController("runner")
@RequestMapping("/api/runner")

public class RunnerController {

    private DiskConfiguration diskConfiguration;
    private RunRepository runRepository;
    private ProblemRepository problemRepository;
    public RunnerController(DiskConfiguration diskConfiguration, RunRepository runRepository, ProblemRepository problemRepository)
    {
        this.diskConfiguration = diskConfiguration;
        this.runRepository = runRepository;
        this.problemRepository = problemRepository;
    }
    @GetMapping
    public String getRuns() {
        return "run";
    }

    @PostMapping
    public SolutionVerdict runSolution(@RequestBody RunRequest runRequest)
    {
        String problemName=problemRepository.findById(runRequest.getProblemId()).get().getName();
        Path executablePath=Path.of(diskConfiguration.getRuns())
                .resolve(runRequest.getRunId().toString().concat(".exe"));
        Integer exitCode=-1;
        ProcessResult result;
        Verdict verdict;
        var testFile=diskConfiguration.getTestsPath()
                .resolve(problemName)
                .resolve(diskConfiguration.getOutputFileName(runRequest.getTestId()))
                .toFile();
        String runMessage="No comment";
        SystemInterpreter interpreter = new SystemInterpreter();
        //TODO: read from DB Source
        var checkerType= CheckerType.Word;
        try(var fileStream=new FileInputStream(testFile))
        {
            result=interpreter.execute(
                    executablePath.toString(),
                    diskConfiguration
                            .getTestsPath()
                            .resolve(problemName)
                            .resolve(diskConfiguration.getInputFileName(runRequest.getTestId()))
                            .toString()
            );
            interpreter.setTimeLimit(runRequest.getTimeLimit());
            exitCode= result.getExitCode().block();
            var foundOutput=result.getOutputStream().readAllBytes();
            var expectedOutput=fileStream.readAllBytes();
            Checker checker = Checker.getInstance(checkerType);
            if(checker.check(foundOutput,expectedOutput))
                verdict=Verdict.ACCEPTED;
            else
                verdict=Verdict.WRONG_ANSWER;
        }
        catch (IOException exception)
        {
            System.err.println(exception.getMessage());
            verdict=Verdict.RUNTIME_ERROR;
            runMessage=exception.getMessage();
        }
        catch(InterruptedException exception)
        {
            verdict=Verdict.TIME_LIMIT_EXCEEDED;
            runMessage="Solution Exceeded Time Limit: (%d s)".formatted(runRequest.getTimeLimit());
        }
        catch(Exception exception)
        {
            verdict=Verdict.INTERNAL_ERROR;
        }

        return new SolutionVerdict(verdict,runMessage);
    }

    @PatchMapping
    public String rerunSolution()
    {
        return "run";
    }
}
