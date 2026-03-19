package aktech.planificador.modules.subject.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.modules.subject.domain.model.SubjectModule;

@Repository
public interface SubjectModuleRepository extends JpaRepository<SubjectModule, UUID> {

    List<SubjectModule> findBySubjectIdOrderByModuleOrderAscCreatedAtAsc(UUID subjectId);

    Optional<SubjectModule> findByIdAndSubjectId(UUID id, UUID subjectId);
}
