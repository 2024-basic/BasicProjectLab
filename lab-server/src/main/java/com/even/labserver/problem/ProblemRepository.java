package com.even.labserver.problem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Integer> {

    /**
     * 주어진 Specification과 Pageable로 문제 목록을 조회합니다.
     * @param spec Specification
     * @param pageable Pageable
     * @return 문제 목록 Page
     */
    Page<Problem> findAll(Specification<Problem> spec, Pageable pageable);

    /**
     * 충남대생이 풀지 않은 문제를 검색 조건에 따라 조회합니다.
     * @return 조건을 만족하는 문제 목록 Page
     */
    @Query("""
SELECT DISTINCT p
FROM Problem p
WHERE p.level BETWEEN :#{#criteria.levelStart} AND :#{#criteria.levelEnd}
  AND p.users IS EMPTY
  AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :#{#criteria.title}, '%')) OR p.problemId = CAST(:#{#criteria.title} AS int))
  AND NOT EXISTS (
      SELECT 1
      FROM AlgorithmTag t
      WHERE t.key IN :#{#criteria.tags}
      AND t.key NOT IN (
          SELECT pt.tag.key
          FROM ProblemAlgorithmTag pt
          WHERE pt.problem = p
      )
  )
  AND p.korean = CASE :#{#criteria.korean}
      WHEN 0 THEN FALSE
      WHEN 1 THEN TRUE
      ELSE p.korean
  END
  AND p.solvable = CASE :#{#criteria.solvable}
      WHEN TRUE THEN TRUE
      ELSE p.solvable
  END
    """)
    Page<Problem> findAllNotSolvedByAllUsers(
            @Param("criteria") ProblemSearchCriteria criteria,
            Pageable pageable
    );

    /**
     * 단 한 명의 충남대생이 푼 문제를 주어진 검색 조건에 따라 조회합니다.
     * @return 조건을 만족하는 문제 목록 Page
     */
    @Query("""
SELECT DISTINCT p
FROM Problem p
WHERE p.level BETWEEN :#{#criteria.levelStart} AND :#{#criteria.levelEnd}
  AND p.usersCount = 1
  AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :#{#criteria.title}, '%')) OR p.problemId = CAST(:#{#criteria.title} AS int))
  AND NOT EXISTS (
        SELECT 1
        FROM AlgorithmTag t
        WHERE t.key IN :#{#criteria.tags}
        AND t.key NOT IN (
            SELECT pt.tag.key
            FROM ProblemAlgorithmTag pt
            WHERE pt.problem = p
        )
      )
  AND p.korean = CASE :#{#criteria.korean}
      WHEN 0 THEN FALSE
      WHEN 1 THEN TRUE
      ELSE p.korean
  END
  AND p.solvable = CASE :#{#criteria.solvable}
      WHEN TRUE THEN TRUE
      ELSE p.solvable
  END
    """)
    Page<Problem> findAllOnlyOneSolved(
            @Param("criteria") ProblemSearchCriteria criteria,
            Pageable pageable
    );

    /**
     * 충남대에서 주어진 사용자만 푼 문제를 주어진 검색 조건에 따라 조회합니다.
     * @return 조건을 만족하는 문제 목록 Page
     */
    @Query("""
SELECT DISTINCT p
FROM Problem p
WHERE p.level BETWEEN :#{#criteria.levelStart} AND :#{#criteria.levelEnd}
  AND p.usersCount = 1
  AND EXISTS (
        SELECT 1
        FROM BojUserProblem up
        WHERE up.user.userId = :#{#criteria.userId}
        AND up.problem = p
      )
  AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :#{#criteria.title}, '%')) OR p.problemId = CAST(:#{#criteria.title} AS int))
  AND NOT EXISTS (
        SELECT 1
        FROM AlgorithmTag t
        WHERE t.key IN :#{#criteria.tags}
        AND t.key NOT IN (
            SELECT pt.tag.key
            FROM ProblemAlgorithmTag pt
            WHERE pt.problem = p
        )
      )
  AND p.korean = CASE :#{#criteria.korean}
      WHEN 0 THEN FALSE
      WHEN 1 THEN TRUE
      ELSE p.korean
  END
  AND p.solvable = CASE :#{#criteria.solvable}
      WHEN TRUE THEN TRUE
      ELSE p.solvable
  END
""")
    Page<Problem> findAllOnlySolvedBy(
            @Param("criteria") ProblemSearchCriteria criteria,
            Pageable pageable
    );

    /**
     * 충남대에서 주어진 사용자만 풀지 않은 문제를 주어진 검색 조건에 따라 조회합니다.
     * @return
     */
    @Query("""
SELECT DISTINCT p
FROM Problem p
WHERE p.level BETWEEN :#{#criteria.levelStart} AND :#{#criteria.levelEnd}
AND p.usersCount >= 1
AND NOT EXISTS (
    SELECT 1
    FROM BojUserProblem up
    WHERE up.user.userId = :#{#criteria.userId}
    AND up.problem = p
)
AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :#{#criteria.title}, '%')) OR p.problemId = CAST(:#{#criteria.title} AS int))
AND NOT EXISTS (
    SELECT 1
    FROM AlgorithmTag t
    WHERE t.key IN :#{#criteria.tags}
    AND t.key NOT IN (
        SELECT pt.tag.key
        FROM ProblemAlgorithmTag pt
        WHERE pt.problem = p
    )
)
AND p.korean = CASE :#{#criteria.korean}
      WHEN 0 THEN FALSE
      WHEN 1 THEN TRUE
      ELSE p.korean
END
AND p.solvable = CASE :#{#criteria.solvable}
      WHEN TRUE THEN TRUE
      ELSE p.solvable
END
""")
    Page<Problem> findAllByOnlyNotSolvedBy(
            @Param("criteria") ProblemSearchCriteria criteria,
            Pageable pageable
    );

    /**
     * 주어진 검색 조건에 따라 문제 목록을 조회합니다.
     * @return
     */
    @Query("""
SELECT DISTINCT p
FROM Problem p
WHERE p.level BETWEEN :#{#criteria.levelStart} AND :#{#criteria.levelEnd}
  AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :#{#criteria.title}, '%')) OR p.problemId = CAST(:#{#criteria.title} AS int))
  AND NOT EXISTS (
      SELECT 1
      FROM AlgorithmTag t
      WHERE t.key IN :#{#criteria.tags}
      AND t.key NOT IN (
          SELECT pt.tag.key
          FROM ProblemAlgorithmTag pt
          WHERE pt.problem = p
      )
  )
  AND p.korean = CASE :#{#criteria.korean}
      WHEN 0 THEN FALSE
      WHEN 1 THEN TRUE
      ELSE p.korean
  END
  AND p.solvable = CASE :#{#criteria.solvable}
      WHEN TRUE THEN TRUE
      ELSE p.solvable
  END
""")
    Page<Problem> findAllSearch(
            @Param("criteria") ProblemSearchCriteria criteria,
            Pageable pageable
    );

    /**
     * 주어진 사용자 ID로 푼 문제의 알고리즘 태그 빈도를 조회합니다.
     * @param userId 사용자 ID
     * @return 태그 빈도 목록
     */
    @Query("select pat.tag.id, count(pat.tag.id) " +
            "from ProblemAlgorithmTag pat " +
            "join pat.problem p " +
            "join p.users u " +
            "where u.user.userId = :userId " +
            "group by pat.tag.id")
    List<Object[]> findTagFrequenciesByUserId(@Param("userId") String userId);

    /**
     * 문제의 알고리즘 태그 빈도를 조회합니다.
     * @return 알고리즘 태그 빈도 목록
     */
    @Query("select pat.tag.id, count(pat.tag.id) " +
            "from ProblemAlgorithmTag pat " +
            "join pat.problem p " +
            "group by pat.tag.id")
    List<Object[]> findTagFrequencies();

    Page<Problem> findAllByModifiedDateBefore(LocalDateTime modifiedDateBefore, Pageable pageable);

    /**
     * 충남대생이 푼 문제의 수를 조회합니다.
     * @return 충남대생이 푼 문제의 수
     */
    Integer countAllByUsersIsNotEmpty();
}
