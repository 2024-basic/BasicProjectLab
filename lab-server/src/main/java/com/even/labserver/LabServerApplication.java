package com.even.labserver;

import com.even.labserver.problem.ProblemDto;
import com.even.labserver.problem.ProblemService;
import com.even.labserver.utils.ScrapeManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LabServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabServerApplication.class, args);
    }

}
