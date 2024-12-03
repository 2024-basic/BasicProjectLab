package com.even.labserver.problem;

import com.even.labserver.bojuser.BojUserProblem;
import com.even.labserver.etc.BaseTimeEntity;
import com.even.labserver.problem.tag.AlgorithmTag;
import com.even.labserver.problem.tag.ProblemAlgorithmTag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 문제 정보를 저장하는 엔티티
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(indexes = {
        @Index(name = "level_idx", columnList = "level"),
        @Index(name = "solved_count_idx", columnList = "solvedCount"),
})
public class Problem extends BaseTimeEntity {
    @Id
    private Integer problemId; // 문제 번호
    @Column(length = 700, nullable = false)
    private String title; // 문제 제목
    @Column(nullable = false)
    private Integer level; // 문제 난이도 (0 ~ 30)
    @Column(nullable = false)
    private Integer solvedCount; // 푼 사람 수
    @Column(nullable = false)
    private Integer votedCount; // 난이도를 평가한 사람 수
    @Column(nullable = false)
    private Boolean givesNoRating; // solved.ac 레이팅을 주는지
    @Column(nullable = false)
    private Double averageTries; // 평균 시도 횟수
//    @ManyToMany(mappedBy = "problems", fetch = FetchType.EAGER)
//    @EqualsAndHashCode.Exclude
//    private List<AlgorithmTag> tags;
    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private List<ProblemAlgorithmTag> tags = new ArrayList<>(); // 문제의 알고리즘 태그

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private List<BojUserProblem> users = new ArrayList<>(); // 문제를 푼 충남대생

    @Column(length = 300)
    private String source; // 문제 출처

    @Formula("(select count(*) from boj_user_problem bup where bup.problem_id = problem_id)")
    private Long usersCount; // 문제를 푼 충남대생 수
}
