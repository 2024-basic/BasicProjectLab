package com.even.labserver.problem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    List<Problem> findAllByLevel(Integer level);

    Page<Problem> findAll(Specification<Problem> spec, Pageable pageable);

    @Query("select pat.tag.id, count(pat.tag.id) " +
            "from ProblemAlgorithmTag pat " +
            "join pat.problem p " +
            "join p.users u " +
            "where u.user.userId = :userId " +
            "group by pat.tag.id")
    List<Object[]> findTagFrequenciesByUserId(@Param("userId") String userId);

    @Query("select pat.tag.id, count(pat.tag.id) " +
            "from ProblemAlgorithmTag pat " +
            "join pat.problem p " +
            "group by pat.tag.id")
    List<Object[]> findTagFrequencies();
}
