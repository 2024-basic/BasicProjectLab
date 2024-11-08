package com.even.labserver.bojuser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BojUserRepository extends JpaRepository<BojUser, Long> {
    Optional<BojUser> findByUserId(String userId);
}
