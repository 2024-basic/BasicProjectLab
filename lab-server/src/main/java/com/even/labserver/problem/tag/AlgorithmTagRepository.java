package com.even.labserver.problem.tag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlgorithmTagRepository extends JpaRepository<AlgorithmTag, Integer> {
    AlgorithmTag findByKey(String key);
    AlgorithmTag findByDisplayName(String displayName);
}
