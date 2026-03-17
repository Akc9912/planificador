package aktech.planificador.modules.subject.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aktech.planificador.modules.subject.dto.SubjectCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectResponseDto;
import aktech.planificador.modules.subject.dto.SubjectUpdateRequestDto;
import aktech.planificador.modules.subject.enums.SubjectApprovalMethod;
import aktech.planificador.modules.subject.enums.SubjectStatus;
import aktech.planificador.modules.subject.model.Subject;
import aktech.planificador.modules.subject.repository.SubjectRepository;
import aktech.planificador.shared.api.SubjectApi;
import aktech.planificador.shared.event.SubjectStatusChangedEvent;
import aktech.planificador.shared.util.ValidationUtils;

@Service
@Transactional(readOnly = true)
public class SubjectService implements SubjectApi {
    private static final int MAX_TEXT_LENGTH = 150;
    private static final int MAX_CODE_LENGTH = 40;
    private static final int MAX_COLOR_LENGTH = 20;
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    private final SubjectRepository subjectRepository;
    private final SubjectCareerAccessService subjectCareerAccessService;
    private final ApplicationEventPublisher eventPublisher;

    public SubjectService(
            SubjectRepository subjectRepository,
            SubjectCareerAccessService subjectCareerAccessService,
            ApplicationEventPublisher eventPublisher) {
        this.subjectRepository = subjectRepository;
        this.subjectCareerAccessService = subjectCareerAccessService;
        this.eventPublisher = eventPublisher;
    }

    public List<SubjectResponseDto> listByCareer(UUID userId, UUID careerId) {
        requireUserId(userId);
        requireCareerId(careerId);
        subjectCareerAccessService.validateCareerOwnership(userId, careerId);
        return subjectRepository.findByCareerId(careerId).stream().map(this::toResponseDto).toList();
    }

    public List<SubjectResponseDto> listByCareerAndStatus(UUID userId, UUID careerId, String rawStatus) {
        requireUserId(userId);
        requireCareerId(careerId);
        String status = SubjectStatus.normalize(rawStatus);
        subjectCareerAccessService.validateCareerOwnership(userId, careerId);
        return subjectRepository.findByCareerIdAndStatus(careerId, status).stream().map(this::toResponseDto).toList();
    }

    public SubjectResponseDto getOwnedOrThrow(UUID subjectId, UUID userId) {
        return toResponseDto(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId));
    }

    public boolean ownsSubject(UUID subjectId, UUID userId) {
        requireSubjectId(subjectId);
        requireUserId(userId);
        return subjectCareerAccessService.userOwnsSubject(userId, subjectId);
    }

    @Transactional
    public SubjectResponseDto createSubject(UUID userId, SubjectCreateRequestDto request) {
        requireUserId(userId);
        ValidationUtils.requireNotNull(request, "El body de creacion es obligatorio");
        requireCareerId(request.getCareerId());

        subjectCareerAccessService.validateCareerOwnership(userId, request.getCareerId());

        Subject subject = new Subject();
        subject.setCareerId(request.getCareerId());
        subject.setName(validateRequiredText(request.getName(), "nombre", MAX_TEXT_LENGTH));
        subject.setCode(validateOptionalText(request.getCode(), "codigo", MAX_CODE_LENGTH));
        subject.setStatus(normalizeStatusOrDefault(request.getStatus()));
        subject.setGrade(validateIntegerGrade(request.getGrade(), "nota"));
        subject.setApprovalMethod(SubjectApprovalMethod.normalize(request.getApprovalMethod()));
        subject.setCorrelatives(toCorrelativeArray(request.getCorrelatives()));
        subject.setYear(validateNonNegativeInteger(request.getYear(), "anio"));
        subject.setSemester(validateNonNegativeInteger(request.getSemester(), "semestre"));
        subject.setEntranceCourse(Boolean.TRUE.equals(request.getEntranceCourse()));
        subject.setHours(validateNonNegativeInteger(request.getHours(), "horas"));
        subject.setCredits(validateNonNegativeInteger(request.getCredits(), "creditos"));
        subject.setColor(normalizeColorOrDefault(request.getColor()));
        subject.setGradeRequiredForPromotion(
                validateDecimalGrade(request.getGradeRequiredForPromotion(), "nota requerida para promocion"));

        return toResponseDto(subjectRepository.save(subject));
    }

    @Transactional
    public SubjectResponseDto updateSubject(UUID subjectId, UUID userId, SubjectUpdateRequestDto request) {
        requireUserId(userId);
        requireSubjectId(subjectId);
        ValidationUtils.requireNotNull(request, "El body de actualizacion es obligatorio");

        Subject subject = subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);
        String oldStatus = subject.getStatus();

        if (request.getName() != null) {
            subject.setName(validateRequiredText(request.getName(), "nombre", MAX_TEXT_LENGTH));
        }

        if (request.getCode() != null) {
            subject.setCode(validateOptionalText(request.getCode(), "codigo", MAX_CODE_LENGTH));
        }

        if (request.getStatus() != null) {
            subject.setStatus(SubjectStatus.normalize(request.getStatus()));
        }

        if (request.getGrade() != null) {
            subject.setGrade(validateIntegerGrade(request.getGrade(), "nota"));
        }

        if (request.getApprovalMethod() != null) {
            subject.setApprovalMethod(SubjectApprovalMethod.normalize(request.getApprovalMethod()));
        }

        if (request.getCorrelatives() != null) {
            subject.setCorrelatives(toCorrelativeArray(request.getCorrelatives()));
        }

        if (request.getYear() != null) {
            subject.setYear(validateNonNegativeInteger(request.getYear(), "anio"));
        }

        if (request.getSemester() != null) {
            subject.setSemester(validateNonNegativeInteger(request.getSemester(), "semestre"));
        }

        if (request.getEntranceCourse() != null) {
            subject.setEntranceCourse(request.getEntranceCourse());
        }

        if (request.getHours() != null) {
            subject.setHours(validateNonNegativeInteger(request.getHours(), "horas"));
        }

        if (request.getCredits() != null) {
            subject.setCredits(validateNonNegativeInteger(request.getCredits(), "creditos"));
        }

        if (request.getColor() != null) {
            subject.setColor(normalizeColorOrDefault(request.getColor()));
        }

        if (request.getGradeRequiredForPromotion() != null) {
            subject.setGradeRequiredForPromotion(
                    validateDecimalGrade(request.getGradeRequiredForPromotion(), "nota requerida para promocion"));
        }

        Subject saved = subjectRepository.save(subject);

        if (!Objects.equals(oldStatus, saved.getStatus())) {
            eventPublisher.publishEvent(new SubjectStatusChangedEvent(
                    saved.getId(),
                    saved.getCareerId(),
                    oldStatus,
                    saved.getStatus()));
        }

        return toResponseDto(saved);
    }

    @Transactional
    public void deleteSubject(UUID subjectId, UUID userId) {
        requireSubjectId(subjectId);
        requireUserId(userId);

        Subject subject = subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);
        subjectRepository.delete(subject);
    }

    @Override
    public boolean existsSubject(UUID subjectId) {
        requireSubjectId(subjectId);
        return subjectRepository.existsById(subjectId);
    }

    @Override
    public boolean userOwnsSubject(UUID userId, UUID subjectId) {
        requireUserId(userId);
        requireSubjectId(subjectId);
        return subjectCareerAccessService.userOwnsSubject(userId, subjectId);
    }

    private String validateRequiredText(String value, String fieldName, int maxLength) {
        return ValidationUtils.requireText(value, fieldName, maxLength);
    }

    private String validateOptionalText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException("El campo " + fieldName + " supera el maximo permitido");
        }

        return normalized;
    }

    private Integer validateIntegerGrade(Integer value, String fieldName) {
        if (value == null) {
            return null;
        }

        if (value < 0 || value > 10) {
            throw new IllegalArgumentException("El campo " + fieldName + " debe estar entre 0 y 10");
        }

        return value;
    }

    private BigDecimal validateDecimalGrade(BigDecimal value, String fieldName) {
        if (value == null) {
            return null;
        }

        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException("El campo " + fieldName + " debe estar entre 0 y 10");
        }

        return value;
    }

    private Integer validateNonNegativeInteger(Integer value, String fieldName) {
        if (value == null) {
            return null;
        }

        if (value < 0) {
            throw new IllegalArgumentException("El campo " + fieldName + " no puede ser negativo");
        }

        return value;
    }

    private String normalizeStatusOrDefault(String rawStatus) {
        if (rawStatus == null) {
            return SubjectStatus.PENDIENTE.getValue();
        }
        return SubjectStatus.normalize(rawStatus);
    }

    private String normalizeColorOrDefault(String rawColor) {
        if (rawColor == null) {
            return "#3B82F6";
        }

        String normalized = validateRequiredText(rawColor, "color", MAX_COLOR_LENGTH);
        if (!HEX_COLOR_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("El campo color debe tener formato hexadecimal #RRGGBB");
        }

        return normalized;
    }

    private String[] toCorrelativeArray(List<UUID> correlatives) {
        if (correlatives == null || correlatives.isEmpty()) {
            return new String[0];
        }

        return correlatives.stream()
                .filter(Objects::nonNull)
                .map(UUID::toString)
                .toArray(String[]::new);
    }

    private List<UUID> toCorrelativeList(String[] correlatives) {
        if (correlatives == null || correlatives.length == 0) {
            return List.of();
        }

        return Arrays.stream(correlatives)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(this::tryParseUuid)
                .filter(Objects::nonNull)
                .toList();
    }

    private UUID tryParseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private SubjectResponseDto toResponseDto(Subject subject) {
        SubjectResponseDto dto = new SubjectResponseDto();
        dto.setId(subject.getId());
        dto.setCareerId(subject.getCareerId());
        dto.setName(subject.getName());
        dto.setCode(subject.getCode());
        dto.setStatus(subject.getStatus());
        dto.setGrade(subject.getGrade());
        dto.setApprovalMethod(subject.getApprovalMethod());
        dto.setCorrelatives(toCorrelativeList(subject.getCorrelatives()));
        dto.setYear(subject.getYear());
        dto.setSemester(subject.getSemester());
        dto.setEntranceCourse(subject.isEntranceCourse());
        dto.setHours(subject.getHours());
        dto.setCredits(subject.getCredits());
        dto.setColor(subject.getColor());
        dto.setGradeRequiredForPromotion(subject.getGradeRequiredForPromotion());
        dto.setCreatedAt(subject.getCreatedAt());
        dto.setUpdatedAt(subject.getUpdatedAt());
        return dto;
    }

    private void requireUserId(UUID userId) {
        ValidationUtils.requireNotNull(userId, "El id de usuario es obligatorio");
    }

    private void requireCareerId(UUID careerId) {
        ValidationUtils.requireNotNull(careerId, "El id de carrera es obligatorio");
    }

    private void requireSubjectId(UUID subjectId) {
        ValidationUtils.requireNotNull(subjectId, "El id de materia es obligatorio");
    }
}
