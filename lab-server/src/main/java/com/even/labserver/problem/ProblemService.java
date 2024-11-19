package com.even.labserver.problem;

import com.even.labserver.problem.tag.AlgorithmTag;
import com.even.labserver.problem.tag.AlgorithmTagDto;
import com.even.labserver.problem.tag.AlgorithmTagRepository;
import com.even.labserver.problem.tag.ProblemAlgorithmTag;
import com.even.labserver.utils.ScrapeManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final AlgorithmTagRepository algorithmTagRepository;

    private final ScrapeManager scrapeManager;

    private Integer startId = 1000;

    public ProblemDto findProblemById(Integer id) {
        var ret = problemRepository.findById(id);
        if (ret.isEmpty()) {
            scrapeProblem(id);
            ret = problemRepository.findById(id);
        }
        return ret.map(ProblemDto::from).orElse(null);
    }

    public List<ProblemDto> scrapeUnscrapedProblems(Integer size) {
        final int maxId = 30000, maxBatch = 500;

        if (size < 1 || size > maxBatch) { throw new IllegalArgumentException("Invalid size"); }
        int cur = startId;
        var toScrape = new ArrayList<Integer>();
        while (cur <= maxId && toScrape.size() < size) {
            if (!existsProblem(cur) && !scrapeManager.isExcluded(cur)) {
                toScrape.add(cur);
            }
            cur++;
        }
        startId = cur;
        var scraped = scrapeManager.getProblems(toScrape);
        var ret = new ArrayList<ProblemDto>(addOrUpdateProblems(scraped));

        return ret;
    }

    public List<ProblemDto> findProblemsRange(Integer start, Integer end) {
        var ret = new ArrayList<ProblemDto>();
        var toScrape = new ArrayList<Integer>();
        for (int i = start; i <= end; ++i) {
            if (!existsProblem(i)) {
                toScrape.add(i);
            } else {
                ret.add(findProblemById(i));
            }
        }

        var scraped = scrapeManager.getProblems(toScrape);
        scraped.forEach(this::addOrUpdateProblem);
        ret.addAll(scraped);

        ret.sort((a, b) -> a.getProblemId() - b.getProblemId());

        return ret;
    }

    public ProblemDto addOrUpdateProblem(ProblemDto dto) {
        try {
            Problem existing = problemRepository.findById(dto.getProblemId()).orElse(null);
            if (existing == null) {
                var problem = fromProblemDto(dto);

//                System.out.println(problem);
                return ProblemDto.from(problemRepository.save(problem));
            } else {
                // TODO: 수정 로직 추가
//                problem.update(dto);
//                handleTags(problem);
//                return ProblemDto.from(problemRepository.save(problem));
                return ProblemDto.from(existing);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ProblemDto> addOrUpdateProblems(List<ProblemDto> dtos) {
        List<Problem> problems = new ArrayList<>();
        for (var dto : dtos) {
            Problem existing = problemRepository.findById(dto.getProblemId()).orElse(null);
            if (existing == null) {
                problems.add(fromProblemDto(dto));
            } else {
                // TODO: 수정 로직 추가
                problems.add(existing);
            }
        }

        List<Problem> saved = problemRepository.saveAll(problems);
        return saved.stream().map(ProblemDto::from).collect(Collectors.toList());
    }

    public ProblemDto scrapeProblem(Integer id) {
        var scraped = scrapeManager.getProblem(String.valueOf(id)).orElse(null);
        if (scraped == null) return null;
        return addOrUpdateProblem(scraped);
    }

    public List<ProblemDto> scrapeRange(Integer startId, Integer endId) {
        var ret = new ArrayList<ProblemDto>();
        var scraped = scrapeManager.getProblemsRange(startId, endId);
        for (var problem : scraped) {
            System.out.println(problem);
            ret.add(addOrUpdateProblem(problem));
        }
        return ret;
    }

    public boolean existsProblem(Integer id) {
        return problemRepository.existsById(id);
    }

    public Problem getProblem(Integer id) {
        return problemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Problem not found: " + id));
    }

    private AlgorithmTag findTagByKey(AlgorithmTagDto dto) {
        var existing = algorithmTagRepository.findByKey(dto.getKey());
        if (existing == null) {
            existing = AlgorithmTag.from(dto);
            existing = algorithmTagRepository.save(existing);
        }
        return existing;
    }

    private Problem fromProblemDto(ProblemDto dto) {
        var ret = Problem.builder()
                .problemId(dto.getProblemId())
                .title(dto.getTitle())
                .level(dto.getLevel())
                .solvedCount(dto.getSolvedCount())
                .votedCount(dto.getVotedCount())
                .givesNoRating(dto.getGivesNoRating())
                .averageTries(dto.getAverageTries())
                .source(dto.getSource())
                .build();
        var tags = Arrays.stream(dto.getTags()).map(x -> {
            var tag = findTagByKey(x);
            return ProblemAlgorithmTag.of(ret, tag);
        }).collect(Collectors.toList());
        ret.setTags(tags);
        return ret;
    }
}
