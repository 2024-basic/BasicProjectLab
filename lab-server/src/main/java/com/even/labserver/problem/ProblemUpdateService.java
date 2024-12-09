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
    public ProblemDto updateProblem(ProblemDto dto) {
        var problem = problemRepository.findById(dto.getProblemId()).orElse(null);
        if (problem == null) return null;
        problem.setTitle(dto.getTitle());
        problem.setLevel(dto.getLevel());
        problem.setSource(dto.getSource());
        problem.setAverageTries(dto.getAverageTries());
        problem.setGivesNoRating(dto.getGivesNoRating());
        problem.setVotedCount(dto.getVotedCount());
        problem.setSolvedCount(dto.getSolvedCount());

        Hibernate.initialize(problem.getTags());
        var existingTags = problem.getTags();
        var newTags = new ArrayList<ProblemAlgorithmTag>();
        for (var tag : dto.getTags()) {
            var existingTag = existingTags.stream().filter(t -> t.getTag().getKey().equals(tag.getKey())).findFirst().orElse(null);
            if (existingTag == null) {
                newTags.add(ProblemAlgorithmTag.builder().problem(problem).tag(findTagByKey(tag)).build());
            } else {
                newTags.add(existingTag);
            }
        }
        problem.setTags(newTags);

        problem = problemRepository.save(problem);
        return ProblemDto.from(problem);
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
