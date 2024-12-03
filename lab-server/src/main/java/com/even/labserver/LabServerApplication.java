package com.even.labserver;

import com.even.labserver.problem.ProblemDto;
import com.even.labserver.problem.ProblemService;
import com.even.labserver.utils.ScrapeManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing // JPA가 자동으로 생성일/수정일을 관리
@EnableScheduling // 스케줄링 기능 활성화
@EnableAsync // 비동기 처리 활성화
@EnableCaching // 캐싱 활성화
public class LabServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabServerApplication.class, args);
    }

}
