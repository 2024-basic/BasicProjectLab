package com.even.labserver.bojuser;

import com.even.labserver.etc.BaseTimeEntity;
import com.even.labserver.problem.Problem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BojUserProblem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private BojUser user;
    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    public static BojUserProblem of(BojUser user, Problem problem) {
        return BojUserProblem.builder()
                .user(user)
                .problem(problem)
                .build();
    }
}
