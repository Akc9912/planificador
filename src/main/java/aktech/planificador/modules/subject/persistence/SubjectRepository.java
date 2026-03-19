package aktech.planificador.modules.subject.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aktech.planificador.modules.subject.domain.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    List<Subject> findByCareerId(UUID careerId);

    List<Subject> findByCareerIdAndStatus(UUID careerId, String status);

    @Query("""
            SELECT s
            FROM Subject s
            WHERE s.careerId = :careerId
                AND (:status IS NULL OR LOWER(s.status) = LOWER(:status))
                AND (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
                AND (:code IS NULL OR LOWER(s.code) LIKE LOWER(CONCAT('%', :code, '%')))
                AND (:year IS NULL OR s.year = :year)
                AND (:semester IS NULL OR s.semester = :semester)
            """)
    List<Subject> searchByCareerWithFilters(
            @Param("careerId") UUID careerId,
            @Param("status") String status,
            @Param("name") String name,
            @Param("code") String code,
            @Param("year") Integer year,
            @Param("semester") Integer semester,
            Sort sort);

    Optional<Subject> findByIdAndCareerId(UUID id, UUID careerId);

    long deleteByCareerId(UUID careerId);
}
