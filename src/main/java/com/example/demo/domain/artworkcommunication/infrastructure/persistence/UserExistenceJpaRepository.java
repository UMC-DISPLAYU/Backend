package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExistenceJpaRepository extends JpaRepository<UserReferenceJpaEntity, Long> {}
