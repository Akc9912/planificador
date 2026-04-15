package com.micarrera.modules.career.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.micarrera.modules.career.entity.Career;
import com.micarrera.modules.career.enums.CareerStatus;

@Repository
public interface CareerRepository extends JpaRepository<Career, UUID> {
    List<Career> findByUserId(UUID userId);

    List<Career> findByStatus(CareerStatus status);

    List<Career> findByUserIdAndStatus(UUID userId, CareerStatus status);

    Optional<Career> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}
