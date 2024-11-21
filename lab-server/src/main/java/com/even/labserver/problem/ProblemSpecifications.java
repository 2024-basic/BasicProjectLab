package com.even.labserver.problem;

import com.even.labserver.bojuser.BojUserProblem;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public class ProblemSpecifications {
    private static final Logger logger = LoggerFactory.getLogger(ProblemSpecifications.class);


    static Specification<Problem> titleLike(String kw) {
        return (b, query, cb) -> {
            query.distinct(true);
            System.out.println("titleLike: " + kw);
            return cb.or(cb.like(b.get("title"), "%" + kw.trim() + "%"));
        };
    }

    static Specification<Problem> levelRange(int levelStart, int levelEnd) {
        return (b, query, cb) -> {
            query.distinct(true);
            return cb.and(cb.greaterThanOrEqualTo(b.get("level"), levelStart), cb.lessThanOrEqualTo(b.get("level"), levelEnd));
        };
    }

    static Specification<Problem> notSolvedBy(String userId) {
        return (b, query, cb) -> {
            query.distinct(true);

            var subquery = query.subquery(Integer.class);
            var subRoot = subquery.from(BojUserProblem.class);

            subquery.select(subRoot.get("problem").get("problemId"))
                    .where(cb.equal(subRoot.get("user").get("userId"), userId));

            return cb.not(b.get("problemId").in(subquery));
        };
    }

    static Specification<Problem> sortedByTagSimilarity(String userId, Map<Integer, Long> userTagWeights, Map<Integer, Long> totalTagProblemCounts) {
        return (b, query, cb) -> {
            // Join 문제-태그 관계
            var tagsJoin = b.join("tags", JoinType.LEFT);
            var tagIdPath = tagsJoin.get("tag").get("id");

            // 유사도 스코어 계산
            var similarityExp = userTagWeights.entrySet().stream()
                    .map(entry -> cb.prod(
                            cb.literal(entry.getValue()), // 태그 빈도
                            cb.equal(tagIdPath, entry.getKey()).as(Integer.class) // 태그 매칭
                    ))
                    .reduce(cb.literal(0), cb::sum).as(Integer.class); // 합계 계산

            // 유사도에 따른 정렬
//            query.orderBy(cb.desc(similarityScore));

            return cb.greaterThan(similarityExp, 0);
        };
    }

}