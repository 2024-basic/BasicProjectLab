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
     * @param title 문제 제목
     * @param levelStart 최저 난이도
     * @param levelEnd 최고 난이도
     * @param tags 태그 목록
     * @param pageable 페이징 정보
     * @return 조건을 만족하는 문제 목록 Page
     */
    @Query("""
    SELECT DISTINCT p
    FROM Problem p
    WHERE p.level BETWEEN :levelStart AND :levelEnd
      AND p.users IS EMPTY
      AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) OR p.problemId = CAST(:title AS int))
      AND NOT EXISTS (
          SELECT 1
          FROM AlgorithmTag t
          WHERE t.key IN :tags
          AND t.key NOT IN (
              SELECT pt.tag.key
              FROM ProblemAlgorithmTag pt
              WHERE pt.problem = p
          )
      )
    """)
    Page<Problem> findAllNotSolvedByAllUsers(
            @Param("title") String title,
            @Param("levelStart") Integer levelStart,
            @Param("levelEnd") Integer levelEnd,
            @Param("tags") List<String> tags,
            Pageable pageable
    );

    /**
     * 단 한 명의 충남대생이 푼 문제를 주어진 검색 조건에 따라 조회합니다.
     * @param title 문제 제목
     * @param levelStart 최저 난이도
     * @param levelEnd 최고 난이도
     * @param tags 태그 목록
     * @param pageable 페이징 정보
     * @return 조건을 만족하는 문제 목록 Page
     */
    @Query("""
    SELECT DISTINCT p
    FROM Problem p
    WHERE p.level BETWEEN :levelStart AND :levelEnd
      AND p.usersCount = 1
      AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) OR p.problemId = CAST(:title AS int))
      AND NOT EXISTS (
            SELECT 1
            FROM AlgorithmTag t
            WHERE t.key IN :tags
            AND t.key NOT IN (
                SELECT pt.tag.key
                FROM ProblemAlgorithmTag pt
                WHERE pt.problem = p
            )
          )
    """)
    Page<Problem> findAllOnlyOneSolved(
            @Param("title") String title,
            @Param("levelStart") Integer levelStart,
            @Param("levelEnd") Integer levelEnd,
            @Param("tags") List<String> tags,
            Pageable pageable
    );

    /**
     * 충남대에서 주어진 사용자만 푼 문제를 주어진 검색 조건에 따라 조회합니다.
     * @param userId 사용자 ID
     * @param title 문제 제목
     * @param levelStart 최저 난이도
     * @param levelEnd 최고 난이도
     * @param tags 태그 목록
     * @param pageable 페이징 정보
     * @return 조건을 만족하는 문제 목록 Page
     */
    @Query("""
    SELECT DISTINCT p
    FROM Problem p
    WHERE p.level BETWEEN :levelStart AND :levelEnd
      AND p.usersCount = 1
      AND EXISTS (
            SELECT 1
            FROM BojUserProblem up
            WHERE up.user.userId = :userId
            AND up.problem = p
          )
      AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) OR p.problemId = CAST(:title AS int))
      AND NOT EXISTS (
            SELECT 1
            FROM AlgorithmTag t
            WHERE t.key IN :tags
            AND t.key NOT IN (
                SELECT pt.tag.key
                FROM ProblemAlgorithmTag pt
                WHERE pt.problem = p
            )
          )
""")
    Page<Problem> findAllOnlySolvedBy(
            @Param("userId") String userId,
            @Param("title") String title,
            @Param("levelStart") Integer levelStart,
            @Param("levelEnd") Integer levelEnd,
            @Param("tags") List<String> tags,
            Pageable pageable
    );

    /**
     * 충남대에서 주어진 사용자만 풀지 않은 문제를 주어진 검색 조건에 따라 조회합니다.
     * @param userId 사용자 ID
     * @param title 문제 제목
     * @param levelStart 최저 난이도
     * @param levelEnd 최고 난이도
     * @param tags 태그 목록
     * @param pageable 페이징 정보
     * @return
     */
    @Query("""
    SELECT DISTINCT p
    FROM Problem p
    WHERE p.level BETWEEN :levelStart AND :levelEnd
    AND p.usersCount >= 1
    AND NOT EXISTS (
        SELECT 1
        FROM BojUserProblem up
        WHERE up.user.userId = :userId
        AND up.problem = p
    )
    AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) OR p.problemId = CAST(:title AS int))
    AND NOT EXISTS (
        SELECT 1
        FROM AlgorithmTag t
        WHERE t.key IN :tags
        AND t.key NOT IN (
            SELECT pt.tag.key
            FROM ProblemAlgorithmTag pt
            WHERE pt.problem = p
        )
    )
""")
    Page<Problem> findAllByOnlyNotSolvedBy(
            @Param("userId") String userId,
            @Param("title") String title,
            @Param("levelStart") Integer levelStart,
            @Param("levelEnd") Integer levelEnd,
            @Param("tags") List<String> tags,
            Pageable pageable
    );

    /**
     * 주어진 검색 조건에 따라 문제 목록을 조회합니다.
     * @param title 문제 제목
     * @param levelStart 최저 난이도
     * @param levelEnd 최고 난이도
     * @param tags 태그 목록
     * @param pageable 페이징 정보
     * @return
     */
    @Query("""
    SELECT DISTINCT p
    FROM Problem p
    WHERE p.level BETWEEN :levelStart AND :levelEnd
      AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) OR p.problemId = CAST(:title AS int))
      AND NOT EXISTS (
          SELECT 1
          FROM AlgorithmTag t
          WHERE t.key IN :tags
          AND t.key NOT IN (
              SELECT pt.tag.key
              FROM ProblemAlgorithmTag pt
              WHERE pt.problem = p
          )
      )
""")
    Page<Problem> findAllSearch(
            @Param("title") String title,
            @Param("levelStart") Integer levelStart,
            @Param("levelEnd") Integer levelEnd,
            @Param("tags") List<String> tags,
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
