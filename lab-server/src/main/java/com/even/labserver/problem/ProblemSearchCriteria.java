package com.even.labserver.problem;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSearchCriteria {
    private String title;
    private Integer levelStart;
    private Integer levelEnd;
    private List<String> tags;
    private Integer korean = -1; // -1: 모두, 0: 한국어 지원 X, 1: 한국어 지원 O

    private String userId;
    private Boolean solvable;

    public static ProblemSearchCriteria from(String str, Integer levelStart, Integer levelEnd, String userId) {
        var tokens = str.split(" ");

        var ret = builder()
                .levelStart(levelStart)
                .levelEnd(levelEnd)
//                .userId(userId.substring("$onlyOneUser".length() + 1))
                .build();

        var tags = new ArrayList<String>();
        var sb = new StringBuilder();

        for (var token : tokens) {
            if (token.startsWith("#")) { // 태그
                tags.add(token.substring(1));
            } else if (token.startsWith("$")) {
                // special keyword
                if (token.equals("$ko")) {
                    ret.setKorean(1);
                } else if (token.equals("$s")) {
                    ret.setSolvable(true);
                }
            }
            else {
                sb.append(token).append(" ");
            }
        }

        ret.setTitle(sb.toString().trim());
        ret.setTags(tags);

        return ret;
    }
}
