package aktech.planificador.modules.subject.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aktech.planificador.modules.subject.dto.SubjectModuleCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectModuleResponseDto;
import aktech.planificador.modules.subject.dto.SubjectModuleUpdateRequestDto;
import aktech.planificador.modules.subject.model.SubjectModule;
import aktech.planificador.modules.subject.repository.SubjectModuleRepository;
import aktech.planificador.shared.exception.NotFoundException;
import aktech.planificador.shared.util.ValidationUtils;

@Service
@Transactional(readOnly = true)
public class SubjectModuleService {
    private static final int MAX_TEXT_LENGTH = 150;

    private final SubjectModuleRepository subjectModuleRepository;
    private final SubjectCareerAccessService subjectCareerAccessService;

    public SubjectModuleService(
            SubjectModuleRepository subjectModuleRepository,
            SubjectCareerAccessService subjectCareerAccessService) {
        this.subjectModuleRepository = subjectModuleRepository;
        this.subjectCareerAccessService = subjectCareerAccessService;
    }

    public List<SubjectModuleResponseDto> listBySubject(UUID userId, UUID subjectId) {
        requireUserId(userId);
        requireSubjectId(subjectId);
        subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);
        return subjectModuleRepository.findBySubjectIdOrderByModuleOrderAscCreatedAtAsc(subjectId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public SubjectModuleResponseDto createModule(UUID userId, UUID subjectId, SubjectModuleCreateRequestDto request) {
        requireUserId(userId);
        requireSubjectId(subjectId);
        ValidationUtils.requireNotNull(request, "El body de creacion es obligatorio");

        subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);

        SubjectModule module = new SubjectModule();
        module.setSubjectId(subjectId);
        module.setName(ValidationUtils.requireText(request.getName(), "nombre", MAX_TEXT_LENGTH));
        module.setGrade(validateGrade(request.getGrade(), "nota"));
        module.setModuleOrder(validateOrderOrDefault(request.getModuleOrder()));

        return toResponseDto(subjectModuleRepository.save(module));
    }

    @Transactional
    public SubjectModuleResponseDto updateModule(
            UUID userId,
            UUID subjectId,
            UUID moduleId,
            SubjectModuleUpdateRequestDto request) {
        requireUserId(userId);
        requireSubjectId(subjectId);
        requireModuleId(moduleId);
        ValidationUtils.requireNotNull(request, "El body de actualizacion es obligatorio");

        subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);

        SubjectModule module = subjectModuleRepository.findByIdAndSubjectId(moduleId, subjectId)
                .orElseThrow(() -> new NotFoundException("Modulo evaluable no encontrado"));

        if (request.getName() != null) {
            module.setName(ValidationUtils.requireText(request.getName(), "nombre", MAX_TEXT_LENGTH));
        }

        if (request.getGrade() != null) {
            module.setGrade(validateGrade(request.getGrade(), "nota"));
        }

        if (request.getModuleOrder() != null) {
            module.setModuleOrder(validateOrderOrDefault(request.getModuleOrder()));
        }

        return toResponseDto(subjectModuleRepository.save(module));
    }

    @Transactional
    public void deleteModule(UUID userId, UUID subjectId, UUID moduleId) {
        requireUserId(userId);
        requireSubjectId(subjectId);
        requireModuleId(moduleId);

        subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);

        SubjectModule module = subjectModuleRepository.findByIdAndSubjectId(moduleId, subjectId)
                .orElseThrow(() -> new NotFoundException("Modulo evaluable no encontrado"));

        subjectModuleRepository.delete(module);
    }

    private BigDecimal validateGrade(BigDecimal value, String fieldName) {
        if (value == null) {
            return null;
        }

        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException("El campo " + fieldName + " debe estar entre 0 y 10");
        }

        return value;
    }

    private Integer validateOrderOrDefault(Integer value) {
        if (value == null) {
            return 0;
        }

        if (value < 0) {
            throw new IllegalArgumentException("El orden del modulo no puede ser negativo");
        }

        return value;
    }

    private SubjectModuleResponseDto toResponseDto(SubjectModule module) {
        SubjectModuleResponseDto dto = new SubjectModuleResponseDto();
        dto.setId(module.getId());
        dto.setSubjectId(module.getSubjectId());
        dto.setName(module.getName());
        dto.setGrade(module.getGrade());
        dto.setModuleOrder(module.getModuleOrder());
        dto.setCreatedAt(module.getCreatedAt());
        dto.setUpdatedAt(module.getUpdatedAt());
        return dto;
    }

    private void requireUserId(UUID userId) {
        ValidationUtils.requireNotNull(userId, "El id de usuario es obligatorio");
    }

    private void requireSubjectId(UUID subjectId) {
        ValidationUtils.requireNotNull(subjectId, "El id de materia es obligatorio");
    }

    private void requireModuleId(UUID moduleId) {
        ValidationUtils.requireNotNull(moduleId, "El id de modulo evaluable es obligatorio");
    }
}
