package aktech.planificador.modules.career.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.modules.career.domain.model.Career;
import aktech.planificador.modules.career.enums.CareerStatus;

@Repository
public interface CareerRepository extends JpaRepository<Career, UUID> {
    List<Career> findByUserId(UUID userId);

    List<Career> findByStatus(CareerStatus status);

    List<Career> findByUserIdAndStatus(UUID userId, CareerStatus status);

    Optional<Career> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}
