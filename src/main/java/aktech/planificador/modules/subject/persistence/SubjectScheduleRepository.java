package aktech.planificador.modules.subject.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.modules.subject.domain.model.SubjectSchedule;

@Repository
public interface SubjectScheduleRepository extends JpaRepository<SubjectSchedule, UUID> {

    List<SubjectSchedule> findBySubjectIdOrderByDayOfWeekAscStartTimeAsc(UUID subjectId);

    Optional<SubjectSchedule> findByIdAndSubjectId(UUID id, UUID subjectId);
}
