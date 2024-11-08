package com.even.labserver.problem.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlgorithmTagDto {
    String displayName;
    Integer problemCount;
    String key;

    public static AlgorithmTagDto from(AlgorithmTag tag) {
        return AlgorithmTagDto.builder()
                .displayName(tag.getDisplayName())
                .problemCount(tag.getProblemCount())
                .key(tag.getKey())
                .build();
    }
}
