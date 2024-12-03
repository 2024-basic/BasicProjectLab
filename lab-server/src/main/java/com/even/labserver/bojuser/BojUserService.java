package com.even.labserver.bojuser;

import com.even.labserver.problem.ProblemRepository;
import com.even.labserver.problem.ProblemService;
import com.even.labserver.utils.ScrapeManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BojUserService {
    private final BojUserRepository bojUserRepository;

    private final ProblemService problemService;
    private final ScrapeManager scrapeManager;
    private Set<String> schoolUsers;

    public BojUserDto findUserById(String userId) {
        var ret = bojUserRepository.findByUserId(userId);
        if (ret.isEmpty()) {
            scrapeManager.getUser(userId).ifPresentOrElse(this::addOrUpdateUser, () -> {
                System.out.println("User not found: " + userId);
            });
            ret = bojUserRepository.findByUserId(userId);
        } else {
//            var today = LocalDateTime.now();
//            var lastModified = ret.get().getModifiedDate();
//            if (today.getDayOfYear() != lastModified.getDayOfYear() || today.getYear() != lastModified.getYear()) {
//                scrapeManager.getUser(userId).ifPresent(this::addOrUpdateUser);
//                ret = bojUserRepository.findByUserId(userId);
//            }
        }

        return ret.map(BojUserDto::from).orElse(null);
    }

    public BojUserDto addOrUpdateUser(BojUserDto dto) {
        try {
            BojUser existing = bojUserRepository.findByUserId(dto.getUserId()).orElse(null);
            if (existing == null) {
                System.out.println("Adding user: " + dto.getUserId());
                var user = BojUser.builder()
                        .userId(dto.getUserId())
                        .level(dto.getLevel())
                        .solved(dto.getSolved())
                        .build();

                var solvedProblems = new HashSet<BojUserProblem>();
                var toScrape = new ArrayList<Integer>();
                for (var problemId : dto.getSolvedProblems()) {
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
                System.out.println("Updating user: " + dto.getUserId());

                existing.setLevel(dto.getLevel());
                existing.setSolved(dto.getSolved());

// 기존 solvedProblems 가져오기
                var existingProblems = existing.getProblems();
                var existingProblemIds = existingProblems.stream()
                        .map(bojUserProblem -> bojUserProblem.getProblem().getProblemId())
                        .collect(Collectors.toSet());

                var toScrape = new ArrayList<Integer>();
                var newSolvedProblems = new HashSet<BojUserProblem>();

                for (var problemId : dto.getSolvedProblems()) {
                    if (!existingProblemIds.contains(problemId)) {
                        if (!problemService.existsProblem(problemId)) {
                            toScrape.add(problemId); // 아직 문제 데이터가 없는 경우 스크래핑 대상에 추가
                        } else {
                            var problem = problemService.getProblem(problemId);
                            newSolvedProblems.add(BojUserProblem.of(existing, problem));
                        }
                    }
                }

// 새로 스크랩한 문제 추가
                var scraped = scrapeManager.getProblems(toScrape);
                scraped.forEach(problemDto -> {
                    var userProblem = BojUserProblem.of(existing, problemService.getProblem(problemService.addOrUpdateProblem(problemDto).getProblemId()));
                    newSolvedProblems.add(userProblem);
                });

                System.out.println("New problems: " + newSolvedProblems.size());

// 기존 문제들과 새롭게 추가된 문제들을 합침
                existingProblems.addAll(newSolvedProblems);
                existing.setProblems(existingProblems);

// 업데이트된 사용자 정보 저장
                return BojUserDto.from(bojUserRepository.save(existing));
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

    public Set<String> addAllUsers() {
        var users = findAllUsers();
        users.forEach(this::findUserById);

        return users;
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void updateAllUsersScheduled() {
        System.out.println("Scheduled update all users... " + LocalDateTime.now());
        updateAllUsers();
    }

    @Async
    public void updateAllUsers() {
        var usersDB = bojUserRepository.findAll();
        var scraped = scrapeManager.getSchoolUsersAndCounts();

        for (var user : usersDB) {
            var userId = user.getUserId();
            var countRecent = scraped.get(userId);
            if (countRecent == null) {
                System.out.println("User not found: " + userId);
                continue;
            } else if (countRecent.equals(user.getSolved())) {
                continue;
            }

            scrapeManager.getUser(userId).ifPresent(this::addOrUpdateUser);
        }

        System.out.println("All users updated. " + LocalDateTime.now());
    }

    public ScrapeManager.SchoolRanking getRanking() {
        return scrapeManager.getSchoolRanking();
    }
}
