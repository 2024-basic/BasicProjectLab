package com.even.labserver.bojuser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BojUserDto {
    private String userId;
    private Integer level;
    private Integer solved;
    private Integer[] solvedProblems;

    public static BojUserDto from(BojUser user) {
        return BojUserDto.builder()
                .userId(user.getUserId())
                .level(user.getLevel())
                .solved(user.getSolved())
                .solvedProblems(user.getProblems().stream().map(x -> x.getProblem().getProblemId()).toArray(Integer[]::new))
                .build();
    }
}
