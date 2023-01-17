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
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.NoSuchElementException;

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
    public SolutionVerdict runSolution(@RequestBody RunRequest runRequest, HttpServletRequest request, HttpServletResponse response)
    {

        registry.counter("run_solution","problem_id",runRequest.getProblemId().toString()).increment();
        String requestId = request.getHeader("X-Request-ID");
        if(requestId == null)
            logger.atWarn().log("Request ID is null");
        String problemName;
        try{
            problemName=problemRepository.findById(runRequest.getProblemId()).orElseThrow(NoSuchElementException::new).getName();
        }
        catch (NoSuchElementException e){
            logger.atWarn().log("Problem with id {} not found",runRequest.getProblemId());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        Path executablePath=Path.of(diskConfiguration.getRuns())
                .resolve(runRequest.getRunId().toString().concat(".exe"));
        logger.atInfo().setMessage("Running solution for problem {} with id {}, Time Limit is {} seconds")
                .addArgument(problemName)
                .addArgument(runRequest.getRunId())
                .addKeyValue("time_limit",runRequest.getTimeLimit())
                .addKeyValue("request_id",requestId)
                .addKeyValue("run_id",runRequest.getRunId())
                .log();
        Integer exitCode=-1;
        ProcessResult result;
        Verdict verdict;
        var testFile=diskConfiguration.getTestsPath()
                .resolve(problemName)
                .resolve(diskConfiguration.getOutputFileName(runRequest.getTestId()))
                .toFile();
        logger.atInfo().setMessage("Test file path {} affected for run id {}")
                .addArgument(testFile.getAbsolutePath())
                .addArgument(runRequest.getRunId())
                .addKeyValue("request_id",requestId)
                .log();
        String runMessage="No comment";
        SystemInterpreter interpreter = new SystemInterpreter();
        //TODO: read from DB Source
        var checkerType= CheckerType.Word;
        try(var fileStream=new FileInputStream(testFile))
        {
            logger.atDebug().setMessage("Beginning execution of solution for run id {}")
                    .addArgument(runRequest.getRunId())
                    .addKeyValue("request_id",requestId)
                    .log();
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
            logger.atInfo().setMessage("Solution for run id {} on test {} exited with code {}")
                    .addArgument(runRequest.getRunId())
                    .addArgument(testFile.getAbsolutePath())
                    .addArgument(exitCode)
                    .addKeyValue("request_id",requestId)
                    .log();
            if(exitCode!=0) {
                verdict = Verdict.RUNTIME_ERROR;
                registry.counter("solution_runs","problem_id",runRequest.getProblemId().toString(),"verdict","runtime_error").increment();
            }
            else {
                logger.atInfo().setMessage("Checking solution correctness for run id {} on test {}")
                        .addArgument(runRequest.getRunId())
                        .addArgument(testFile.getAbsolutePath())
                        .addKeyValue("request_id", requestId)
                        .log();
                var foundOutput = result.getOutputStream().readAllBytes();
                var expectedOutput = fileStream.readAllBytes();
                Checker checker = Checker.getInstance(checkerType);
                if (checker.check(foundOutput, expectedOutput)) {
                    verdict = Verdict.ACCEPTED;
                    registry.counter("solution_runs","problem_id",runRequest.getProblemId().toString(),"verdict","accepted").increment();
                }
                else {
                    verdict = Verdict.WRONG_ANSWER;
                    registry.counter("solution_runs","problem_id",runRequest.getProblemId().toString(),"verdict","wrong_answer").increment();
                }
            }
        }
        catch (IOException exception)
        {
            logger.atError().setMessage("Error while reading test file for run id {}")
                    .addArgument(runRequest.getRunId())
                    .addKeyValue("request_id",requestId)
                    .log();
            System.err.println(exception.getMessage());
            verdict=Verdict.RUNTIME_ERROR;
            registry.counter("solution_runs","problem_id",runRequest.getProblemId().toString(),"verdict","runtime_error").increment();
            runMessage=exception.getMessage();
        }
        catch(InterruptedException exception)
        {
            logger.atWarn().setMessage("Solution for run id {} on test {} interrupted")
                    .addArgument(runRequest.getRunId())
                    .addKeyValue("request_id",requestId)
                    .addKeyValue("time_limit",runRequest.getTimeLimit())
                    .log();
            verdict=Verdict.TIME_LIMIT_EXCEEDED;
            registry.counter("solution_runs","problem_id",runRequest.getProblemId().toString(),"verdict","time_limit_exceeded").increment();
            runMessage="Solution Exceeded Time Limit: (%d s)".formatted(runRequest.getTimeLimit());
        }
        catch(Exception exception)
        {
            logger.atError().setMessage("Error while executing solution for run id {}")
                    .addArgument(runRequest.getRunId())
                    .addKeyValue("request_id",requestId)
                    .log();
            verdict=Verdict.INTERNAL_ERROR;
            runMessage=exception.getMessage();
            registry.counter("solution_runs","problem_id",runRequest.getProblemId().toString(),"verdict","internal_error").increment();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return new SolutionVerdict(verdict,runMessage);
    }

    @PatchMapping
    public String rerunSolution()
    {
        return "run";
    }
}
