package com.even.labserver.problem.tag;

import com.even.labserver.etc.BaseTimeEntity;
import com.even.labserver.problem.Problem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemAlgorithmTag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private AlgorithmTag tag;

    public static ProblemAlgorithmTag of(Problem problem, AlgorithmTag tag) {
        return ProblemAlgorithmTag.builder()
                .problem(problem)
                .tag(tag)
                .build();
    }
}
