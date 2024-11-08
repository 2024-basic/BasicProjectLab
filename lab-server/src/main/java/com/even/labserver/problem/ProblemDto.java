package com.even.labserver.problem;

import com.even.labserver.problem.tag.AlgorithmTag;
import com.even.labserver.problem.tag.AlgorithmTagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDto {
    private Integer problemId;
    private String title;
    private Integer level;
    private Integer solvedCount;
    private Integer votedCount;
    private Boolean givesNoRating;
    private Double averageTries;
    private AlgorithmTagDto[] tags;
    private String source;

    public static ProblemDto from(Problem problem) {
        return ProblemDto.builder()
                .problemId(problem.getProblemId())
                .title(problem.getTitle())
                .level(problem.getLevel())
                .solvedCount(problem.getSolvedCount())
                .votedCount(problem.getVotedCount())
                .givesNoRating(problem.getGivesNoRating())
                .averageTries(problem.getAverageTries())
                .tags(problem.getTags().stream().map(x -> AlgorithmTagDto.from(x.getTag())).toArray(AlgorithmTagDto[]::new))
                .source(problem.getSource())
                .build();
    }
}
