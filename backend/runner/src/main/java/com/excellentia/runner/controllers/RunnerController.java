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
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@RestController("runner")
@RequestMapping("/runner")

public class RunnerController {
    Logger logger = LoggerFactory.getLogger(RunnerController.class);

    private DiskConfiguration diskConfiguration;
    private RunRepository runRepository;
    private ProblemRepository problemRepository;
    private final MeterRegistry registry;

    public RunnerController(DiskConfiguration diskConfiguration, RunRepository runRepository, ProblemRepository problemRepository,
                            MeterRegistry registry) {
        this.diskConfiguration = diskConfiguration;
        this.runRepository = runRepository;
        this.problemRepository = problemRepository;
        this.registry = registry;
    }
    @GetMapping
    public String getRuns() {
        return "run";
    }

    @PostMapping
    public SolutionVerdict runSolution(@RequestBody RunRequest runRequest)
    {
        registry.counter("run_solution","problem_id",runRequest.getProblemId().toString()).increment();

        String problemName=problemRepository.findById(runRequest.getProblemId()).get().getName();
        Path executablePath=Path.of(diskConfiguration.getRuns())
                .resolve(runRequest.getRunId().toString().concat(".exe"));
        logger.info("Running solution for problem {} with id {}, Time Limit is {} seconds",problemName,runRequest.getRunId(),runRequest.getTimeLimit());
        Integer exitCode=-1;
        ProcessResult result;
        Verdict verdict;
        var testFile=diskConfiguration.getTestsPath()
                .resolve(problemName)
                .resolve(diskConfiguration.getOutputFileName(runRequest.getTestId()))
                .toFile();
        logger.info("Test file path {} affected for run id {}",testFile.getAbsolutePath(),runRequest.getRunId());
        String runMessage="No comment";
        SystemInterpreter interpreter = new SystemInterpreter();
        //TODO: read from DB Source
        var checkerType= CheckerType.Word;
        try(var fileStream=new FileInputStream(testFile))
        {
            logger.debug("Beginning execution of solution for run id {}",runRequest.getRunId());
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
            logger.info("Solution for run id {} on test {} exited with code {}",runRequest.getRunId(),testFile.getAbsolutePath(),exitCode);
            if(exitCode!=0)
            {
                verdict=Verdict.RUNTIME_ERROR;
            }
            else
            {
                logger.info("Checking solution correctness for run id {} on test {}",runRequest.getRunId(),testFile.getAbsolutePath());
            }
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
            logger.error("Solution for run id {} on test {} was interrupted",runRequest.getRunId(),testFile.getAbsolutePath());
            verdict=Verdict.INTERNAL_ERROR;
            runMessage="Solution Exceeded Time Limit: (%d s)".formatted(runRequest.getTimeLimit());
        }
        catch(Exception exception)
        {
            logger.error("Solution for run id {} on test {} is getting a runtime error",runRequest.getRunId(),testFile.getAbsolutePath());
            verdict=Verdict.INTERNAL_ERROR;
            runMessage=exception.getMessage();
        }

        return new SolutionVerdict(verdict,runMessage);
    }

    @PatchMapping
    public String rerunSolution()
    {
        return "run";
    }
}
