package aktech.planificador.modules.equivalence.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aktech.planificador.modules.equivalence.domain.model.Equivalence;

@Repository
public interface EquivalenceRepository extends JpaRepository<Equivalence, UUID> {

  List<Equivalence> findByUserIdOrderByCreatedAtDesc(UUID userId);

  Optional<Equivalence> findByIdAndUserId(UUID id, UUID userId);

  boolean existsByUserIdAndSubjectAIdAndSubjectBId(UUID userId, UUID subjectAId, UUID subjectBId);

  boolean existsByUserIdAndSubjectAIdAndSubjectBIdAndIdNot(
      UUID userId,
      UUID subjectAId,
      UUID subjectBId,
      UUID id);

  @Query("""
      select e
      from Equivalence e
      where e.userId = :userId
        and (e.subjectAId = :subjectId or e.subjectBId = :subjectId)
      order by e.createdAt desc
      """)
  List<Equivalence> findByUserIdAndSubject(
      @Param("userId") UUID userId,
      @Param("subjectId") UUID subjectId);
}
