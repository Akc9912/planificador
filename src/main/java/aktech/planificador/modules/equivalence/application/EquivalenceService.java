package aktech.planificador.modules.equivalence.application;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aktech.planificador.modules.equivalence.domain.model.Equivalence;
import aktech.planificador.modules.equivalence.dto.EquivalenceCreateRequestDto;
import aktech.planificador.modules.equivalence.dto.EquivalenceResponseDto;
import aktech.planificador.modules.equivalence.dto.EquivalenceUpdateRequestDto;
import aktech.planificador.modules.equivalence.enums.EquivalenceType;
import aktech.planificador.modules.equivalence.persistence.EquivalenceRepository;
import aktech.planificador.shared.api.SubjectApi;
import aktech.planificador.shared.dto.SubjectBasicDto;
import aktech.planificador.shared.exception.BusinessException;
import aktech.planificador.shared.exception.NotFoundException;
import aktech.planificador.shared.util.ValidationUtils;

@Service
@Transactional(readOnly = true)
public class EquivalenceService {
    private static final int MAX_NOTES_LENGTH = 2000;

    private final EquivalenceRepository equivalenceRepository;
    private final SubjectApi subjectApi;

    public EquivalenceService(EquivalenceRepository equivalenceRepository, SubjectApi subjectApi) {
        this.equivalenceRepository = equivalenceRepository;
        this.subjectApi = subjectApi;
    }

    public List<EquivalenceResponseDto> listByUser(UUID userId) {
        requireUserId(userId);
        return equivalenceRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public List<EquivalenceResponseDto> listBySubject(UUID userId, UUID subjectId) {
        requireUserId(userId);
        requireSubjectId(subjectId, "A");

        if (!subjectApi.userOwnsSubject(userId, subjectId)) {
            throw new NotFoundException("Materia no encontrada o sin permisos");
        }

        return equivalenceRepository.findByUserIdAndSubject(userId, subjectId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public EquivalenceResponseDto getOwnedOrThrow(UUID id, UUID userId) {
        return toResponseDto(getOwnedEntityOrThrow(id, userId));
    }

    @Transactional
    public EquivalenceResponseDto createEquivalence(UUID userId, EquivalenceCreateRequestDto request) {
        requireUserId(userId);
        ValidationUtils.requireNotNull(request, "El body de creacion es obligatorio");

        UUID subjectAId = request.getSubjectAId();
        UUID subjectBId = request.getSubjectBId();
        requireSubjectId(subjectAId, "A");
        requireSubjectId(subjectBId, "B");

        validateNoCycle(subjectAId, subjectBId);
        validateSubjectOwnership(userId, subjectAId, subjectBId);
        validateCareerConsistency(subjectAId, subjectBId);
        validateNoDuplicatePair(userId, subjectAId, subjectBId, null);

        Equivalence equivalence = new Equivalence();
        equivalence.setUserId(userId);
        equivalence.setType(EquivalenceType.normalizeRequired(request.getType()));
        equivalence.setSubjectAId(subjectAId);
        equivalence.setSubjectBId(subjectBId);
        equivalence.setNotes(normalizeNotes(request.getNotes()));

        return toResponseDto(equivalenceRepository.save(equivalence));
    }

    @Transactional
    public EquivalenceResponseDto updateEquivalence(UUID id, UUID userId, EquivalenceUpdateRequestDto request) {
        requireEquivalenceId(id);
        requireUserId(userId);
        ValidationUtils.requireNotNull(request, "El body de actualizacion es obligatorio");

        Equivalence equivalence = getOwnedEntityOrThrow(id, userId);

        UUID subjectAId = request.getSubjectAId() != null ? request.getSubjectAId() : equivalence.getSubjectAId();
        UUID subjectBId = request.getSubjectBId() != null ? request.getSubjectBId() : equivalence.getSubjectBId();

        requireSubjectId(subjectAId, "A");
        requireSubjectId(subjectBId, "B");

        validateNoCycle(subjectAId, subjectBId);
        validateSubjectOwnership(userId, subjectAId, subjectBId);
        validateCareerConsistency(subjectAId, subjectBId);
        validateNoDuplicatePair(userId, subjectAId, subjectBId, id);

        if (request.getType() != null) {
            equivalence.setType(EquivalenceType.normalizeRequired(request.getType()));
        }

        if (request.getSubjectAId() != null) {
            equivalence.setSubjectAId(request.getSubjectAId());
        }

        if (request.getSubjectBId() != null) {
            equivalence.setSubjectBId(request.getSubjectBId());
        }

        if (request.getNotes() != null) {
            equivalence.setNotes(normalizeNotes(request.getNotes()));
        }

        return toResponseDto(equivalenceRepository.save(equivalence));
    }

    @Transactional
    public void deleteEquivalence(UUID id, UUID userId) {
        Equivalence equivalence = getOwnedEntityOrThrow(id, userId);
        equivalenceRepository.delete(equivalence);
    }

    public void validateNoCycle(UUID sourceSubjectId, UUID targetSubjectId) {
        if (Objects.equals(sourceSubjectId, targetSubjectId)) {
            throw new BusinessException("Una materia no puede ser equivalente a si misma");
        }
    }

    private void validateSubjectOwnership(UUID userId, UUID subjectAId, UUID subjectBId) {
        if (!subjectApi.userOwnsSubject(userId, subjectAId)) {
            throw new NotFoundException("Materia A no encontrada o sin permisos");
        }

        if (!subjectApi.userOwnsSubject(userId, subjectBId)) {
            throw new NotFoundException("Materia B no encontrada o sin permisos");
        }
    }

    private void validateCareerConsistency(UUID subjectAId, UUID subjectBId) {
        SubjectBasicDto subjectA = subjectApi.getSubjectBasic(subjectAId);
        SubjectBasicDto subjectB = subjectApi.getSubjectBasic(subjectBId);

        if (subjectA.getCareerId() != null && subjectA.getCareerId().equals(subjectB.getCareerId())) {
            throw new BusinessException("Las equivalencias deben vincular materias de carreras distintas");
        }
    }

    private void validateNoDuplicatePair(UUID userId, UUID subjectAId, UUID subjectBId, UUID currentEquivalenceId) {
        boolean existsDirect;
        boolean existsReverse;

        if (currentEquivalenceId == null) {
            existsDirect = equivalenceRepository.existsByUserIdAndSubjectAIdAndSubjectBId(userId, subjectAId,
                    subjectBId);
            existsReverse = equivalenceRepository.existsByUserIdAndSubjectAIdAndSubjectBId(userId, subjectBId,
                    subjectAId);
        } else {
            existsDirect = equivalenceRepository.existsByUserIdAndSubjectAIdAndSubjectBIdAndIdNot(
                    userId,
                    subjectAId,
                    subjectBId,
                    currentEquivalenceId);
            existsReverse = equivalenceRepository.existsByUserIdAndSubjectAIdAndSubjectBIdAndIdNot(
                    userId,
                    subjectBId,
                    subjectAId,
                    currentEquivalenceId);
        }

        if (existsDirect || existsReverse) {
            throw new BusinessException("Ya existe una equivalencia entre las materias indicadas");
        }
    }

    private Equivalence getOwnedEntityOrThrow(UUID id, UUID userId) {
        requireEquivalenceId(id);
        requireUserId(userId);
        return equivalenceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Equivalencia no encontrada o sin permisos"));
    }

    private String normalizeNotes(String notes) {
        if (notes == null) {
            return null;
        }

        String normalized = notes.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > MAX_NOTES_LENGTH) {
            throw new IllegalArgumentException("El campo notas supera el maximo permitido");
        }

        return normalized;
    }

    private EquivalenceResponseDto toResponseDto(Equivalence equivalence) {
        EquivalenceResponseDto dto = new EquivalenceResponseDto();
        dto.setId(equivalence.getId());
        dto.setUserId(equivalence.getUserId());
        dto.setType(equivalence.getType());
        dto.setSubjectAId(equivalence.getSubjectAId());
        dto.setSubjectBId(equivalence.getSubjectBId());
        dto.setNotes(equivalence.getNotes());
        dto.setCreatedAt(equivalence.getCreatedAt());
        dto.setUpdatedAt(equivalence.getUpdatedAt());
        return dto;
    }

    private void requireEquivalenceId(UUID id) {
        ValidationUtils.requireNotNull(id, "El id de equivalencia es obligatorio");
    }

    private void requireUserId(UUID userId) {
        ValidationUtils.requireNotNull(userId, "El id de usuario es obligatorio");
    }

    private void requireSubjectId(UUID subjectId, String alias) {
        ValidationUtils.requireNotNull(subjectId, "El id de materia " + alias + " es obligatorio");
    }
}
