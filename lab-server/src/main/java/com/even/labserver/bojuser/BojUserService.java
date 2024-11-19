package com.even.labserver.bojuser;

import com.even.labserver.problem.ProblemRepository;
import com.even.labserver.problem.ProblemService;
import com.even.labserver.utils.ScrapeManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BojUserService {
    private final BojUserRepository bojUserRepository;
    private final ProblemRepository problemRepository;

    private final ProblemService problemService;
    private final ScrapeManager scrapeManager;
    private Set<String> schoolUsers;

    public BojUserDto findUserById(String userId) {
        var ret = bojUserRepository.findByUserId(userId);
        if (ret.isEmpty()) {
            scrapeManager.getUser(userId).ifPresent(this::addOrUpdateUser);
            ret = bojUserRepository.findByUserId(userId);
        }

        return ret.map(BojUserDto::from).orElse(null);
    }

    public BojUserDto addOrUpdateUser(BojUserDto dto) {
        try {
            BojUser existing = bojUserRepository.findByUserId(dto.getUserId()).orElse(null);
            if (existing == null) {
                var user = BojUser.builder()
                        .userId(dto.getUserId())
                        .level(dto.getLevel())
                        .solved(dto.getSolved())
                        .build();

                var solvedProblems = new HashSet<BojUserProblem>();
                var toScrape = new ArrayList<Integer>();
                for (var problemId : dto.getSolvedProblems()) {
//                    var problem = problemRepository.findById(problemService.findProblemById(problemId).getProblemId());
//                    if (problem.isEmpty()) continue;
//                    var userProblem = BojUserProblem.of(user, problem.get());
//                    solvedProblems.add(userProblem);
                    if (!problemService.existsProblem(problemId)) {
                        toScrape.add(problemId);
                    } else {
                        var problem = problemService.getProblem(problemService.findProblemById(problemId).getProblemId());
                        solvedProblems.add(BojUserProblem.of(user, problem));
                    }
                }

                var scraped = scrapeManager.getProblems(toScrape);
                scraped.forEach(problemDto -> {
                    var userProblem = BojUserProblem.of(user, problemService.getProblem(problemService.addOrUpdateProblem(problemDto).getProblemId()));
                    solvedProblems.add(userProblem);
                });

                user.setProblems(solvedProblems);

                bojUserRepository.save(user);
                return BojUserDto.from(user);
            } else {
                // TODO: 유저 정보 갱신은 지원안함
//                existing.setLevel(dto.getLevel());
//                existing.setSolved(dto.getSolved());
//                bojUserRepository.save(existing);
                return BojUserDto.from(existing);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> findAllUsers() {
        if (schoolUsers == null) {
            schoolUsers = new HashSet<>();
            var scraped = scrapeManager.getSchoolUsers();
            schoolUsers.addAll(scraped);
        }

        return schoolUsers;
    }
}
