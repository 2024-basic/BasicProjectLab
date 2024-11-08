package com.even.labserver;

import com.even.labserver.problem.ProblemService;
import com.even.labserver.utils.ScrapeManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class LabServerApplicationTests {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ScrapeManager scrapeManager;

    @Test
    void scrapeTest() {

        problemService.findProblemById(1000);
    }

}
