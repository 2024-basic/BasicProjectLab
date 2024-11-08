package com.even.labserver.problem.tag;

import com.even.labserver.etc.BaseTimeEntity;
import com.even.labserver.problem.Problem;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlgorithmTag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 100)
    private String displayName;
    @Column(nullable = false)
    private Integer problemCount;
    @Column(nullable = false, length = 100, name = "tag_key")
    private String key;

    @OneToMany(mappedBy = "tag", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ProblemAlgorithmTag> problems;

    public static AlgorithmTag from(AlgorithmTagDto dto) {
        return AlgorithmTag.builder()
                .displayName(dto.getDisplayName())
                .problemCount(dto.getProblemCount())
                .key(dto.getKey())
                .build();
    }
}
