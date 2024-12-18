package com.even.labserver.problem;

import com.even.labserver.problem.tag.AlgorithmTag;
import com.even.labserver.problem.tag.AlgorithmTagDto;
import com.even.labserver.problem.tag.AlgorithmTagRepository;
import com.even.labserver.problem.tag.ProblemAlgorithmTag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemUpdateService {
    private final ProblemRepository problemRepository;
    private final AlgorithmTagRepository algorithmTagRepository;

    @Transactional
    public Problem getProblemWithTagsById(Integer problemId) {
        var ret = problemRepository.findById(problemId).orElse(null);
        if (ret == null) return null;
        Hibernate.initialize(ret.getTags());
        return ret;
    }

    @Transactional
    public List<ProblemDto> updateProblems(List<ProblemDto> dtoList) {
        List<Problem> problems = new ArrayList<>();
        for (var dto : dtoList) {
            var problem = problemRepository.findById(dto.getProblemId()).orElse(null);
            if (problem == null) continue;
            apply(problem, dto);
            problems.add(problem);
        }

        problems = problemRepository.saveAll(problems);

        return problems.stream().map(ProblemDto::from).toList();
    }

    @Transactional
    public ProblemDto updateProblem(ProblemDto dto) {
        var problem = problemRepository.findById(dto.getProblemId()).orElse(null);
        if (problem == null) return null;
        apply(problem, dto);
        problem = problemRepository.save(problem);
        return ProblemDto.from(problem);
    }

    @Transactional
    protected void apply(Problem orig, ProblemDto dto) {
        orig.setTitle(dto.getTitle());
        orig.setLevel(dto.getLevel());
        orig.setSource(dto.getSource());
        orig.setAverageTries(dto.getAverageTries());
        orig.setGivesNoRating(dto.getGivesNoRating());
        orig.setVotedCount(dto.getVotedCount());
        orig.setSolvedCount(dto.getSolvedCount());
        orig.setKorean(dto.getKorean());
        orig.setSolvable(dto.getSolvable());

        Hibernate.initialize(orig.getTags());
        var existingTags = orig.getTags();
        var newTags = new ArrayList<ProblemAlgorithmTag>();
        for (var tag : dto.getTags()) {
            var existingTag = existingTags.stream().filter(t -> t.getTag().getKey().equals(tag.getKey())).findFirst().orElse(null);
            if (existingTag == null) {
                newTags.add(ProblemAlgorithmTag.builder().problem(orig).tag(findTagByKey(tag)).build());
            } else {
                newTags.add(existingTag);
            }
        }
        orig.setTags(newTags);
    }

    private AlgorithmTag findTagByKey(AlgorithmTagDto dto) {
        var existing = algorithmTagRepository.findByKey(dto.getKey());
        if (existing == null) {
            existing = AlgorithmTag.from(dto);
            existing = algorithmTagRepository.save(existing);
        }
        return existing;
    }
}
