package com.even.labserver.problem;

import com.even.labserver.bojuser.BojUserProblem;
import com.even.labserver.etc.BaseTimeEntity;
import com.even.labserver.problem.tag.AlgorithmTag;
import com.even.labserver.problem.tag.ProblemAlgorithmTag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem extends BaseTimeEntity {
    @Id
    private Integer problemId;
    @Column(length = 700, nullable = false)
    private String title;
    @Column(nullable = false)
    private Integer level;
    @Column(nullable = false)
    private Integer solvedCount;
    @Column(nullable = false)
    private Integer votedCount;
    @Column(nullable = false)
    private Boolean givesNoRating;
    @Column(nullable = false)
    private Double averageTries;
//    @ManyToMany(mappedBy = "problems", fetch = FetchType.EAGER)
//    @EqualsAndHashCode.Exclude
//    private List<AlgorithmTag> tags;
    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private List<ProblemAlgorithmTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private List<BojUserProblem> users = new ArrayList<>();

    @Column(length = 300)
    private String source;

    public void update(ProblemDto dto) {
        if (dto.getTitle() != null) this.title = dto.getTitle();
        if (dto.getLevel() != null) this.level = dto.getLevel();
        if (dto.getSolvedCount() != null) this.solvedCount = dto.getSolvedCount();
        if (dto.getVotedCount() != null) this.votedCount = dto.getVotedCount();
        if (dto.getGivesNoRating() != null) this.givesNoRating = dto.getGivesNoRating();
        if (dto.getAverageTries() != null) this.averageTries = dto.getAverageTries();
//        if (dto.getTags() != null) this.tags = Arrays.stream(dto.getTags()).map(AlgorithmTag::from).toList();
        if (dto.getSource() != null) this.source = dto.getSource();
    }

//    public static Problem from(ProblemDto dto) {
//        return Problem.builder()
//                .problemId(dto.getProblemId())
//                .title(dto.getTitle())
//                .level(dto.getLevel())
//                .solvedCount(dto.getSolvedCount())
//                .votedCount(dto.getVotedCount())
//                .givesNoRating(dto.getGivesNoRating())
//                .averageTries(dto.getAverageTries())
//                .tags(Arrays.stream(dto.getTags()).map(AlgorithmTag::from).toList())
//                .source(dto.getSource())
//                .build();
//    }
}
