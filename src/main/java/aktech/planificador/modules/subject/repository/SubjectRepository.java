package aktech.planificador.modules.subject.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.modules.subject.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    List<Subject> findByCareerId(UUID careerId);

    List<Subject> findByCareerIdAndStatus(UUID careerId, String status);

    Optional<Subject> findByIdAndCareerId(UUID id, UUID careerId);

    long deleteByCareerId(UUID careerId);
}
