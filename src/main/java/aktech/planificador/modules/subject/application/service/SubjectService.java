package aktech.planificador.modules.subject.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aktech.planificador.modules.subject.dto.CareerProgressResponseDto;
import aktech.planificador.modules.subject.dto.SubjectCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectAvailabilityResponseDto;
import aktech.planificador.modules.subject.dto.SubjectResponseDto;
import aktech.planificador.modules.subject.dto.SubjectUpdateRequestDto;
import aktech.planificador.modules.subject.enums.SubjectApprovalMethod;
import aktech.planificador.modules.subject.enums.SubjectStatus;
import aktech.planificador.modules.subject.model.Subject;
import aktech.planificador.modules.subject.repository.SubjectRepository;
import aktech.planificador.shared.api.SubjectApi;
import aktech.planificador.shared.dto.SubjectBasicDto;
import aktech.planificador.shared.event.SubjectStatusChangedEvent;
import aktech.planificador.shared.exception.BusinessException;
import aktech.planificador.shared.exception.NotFoundException;
import aktech.planificador.shared.util.ValidationUtils;

@Service
@Transactional(readOnly = true)
public class SubjectService implements SubjectApi {
    private static final int MAX_TEXT_LENGTH = 150;
    private static final int MAX_CODE_LENGTH = 40;
    private static final int MAX_COLOR_LENGTH = 20;
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "name",
            "code",
            "status",
            "year",
            "semester",
            "credits",
            "hours",
            "createdAt",
            "updatedAt");

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

    public List<SubjectResponseDto> listByCareerWithFilters(
            UUID userId,
            UUID careerId,
            String rawName,
            String rawCode,
            String rawStatus,
            Integer year,
            Integer semester,
            String rawSortBy,
            String rawSortDirection) {
        requireUserId(userId);
        requireCareerId(careerId);
        subjectCareerAccessService.validateCareerOwnership(userId, careerId);

        String name = normalizeSearchTerm(rawName);
        String code = normalizeSearchTerm(rawCode);
        String status = normalizeStatusFilter(rawStatus);
        Integer normalizedYear = validateNonNegativeInteger(year, "anio");
        Integer normalizedSemester = validateNonNegativeInteger(semester, "semestre");
        Sort sort = resolveSort(rawSortBy, rawSortDirection);

        return subjectRepository.searchByCareerWithFilters(
                careerId,
                status,
                name,
                code,
                normalizedYear,
                normalizedSemester,
                sort).stream().map(this::toResponseDto).toList();
    }

    public CareerProgressResponseDto getCareerProgress(UUID userId, UUID careerId) {
        requireUserId(userId);
        requireCareerId(careerId);
        subjectCareerAccessService.validateCareerOwnership(userId, careerId);

        List<Subject> careerSubjects = subjectRepository.findByCareerId(careerId);
        Map<UUID, Subject> subjectsById = toSubjectMap(careerSubjects);

        int totalSubjects = careerSubjects.size();
        int approvedSubjects = countByStatus(careerSubjects, SubjectStatus.APROBADA);
        int pendingSubjects = countByStatus(careerSubjects, SubjectStatus.PENDIENTE);
        int inProgressSubjects = countByStatus(careerSubjects, SubjectStatus.CURSANDO);
        int regularSubjects = countByStatus(careerSubjects, SubjectStatus.REGULAR);
        int libreSubjects = countByStatus(careerSubjects, SubjectStatus.LIBRE);

        int blockedSubjects = (int) careerSubjects.stream()
                .filter(subject -> !isApprovedStatus(subject.getStatus()))
                .filter(subject -> isBlocked(subject, subjectsById, null, null))
                .count();
        int availableSubjects = Math.max(0, (totalSubjects - approvedSubjects) - blockedSubjects);

        int totalCredits = sumMetric(careerSubjects, Subject::getCredits);
        int approvedCredits = sumApprovedMetric(careerSubjects, Subject::getCredits);
        int totalHours = sumMetric(careerSubjects, Subject::getHours);
        int approvedHours = sumApprovedMetric(careerSubjects, Subject::getHours);

        CareerProgressResponseDto dto = new CareerProgressResponseDto();
        dto.setCareerId(careerId);
        dto.setTotalSubjects(totalSubjects);
        dto.setApprovedSubjects(approvedSubjects);
        dto.setPendingSubjects(pendingSubjects);
        dto.setInProgressSubjects(inProgressSubjects);
        dto.setRegularSubjects(regularSubjects);
        dto.setLibreSubjects(libreSubjects);
        dto.setBlockedSubjects(blockedSubjects);
        dto.setAvailableSubjects(availableSubjects);
        dto.setTotalCredits(totalCredits);
        dto.setApprovedCredits(approvedCredits);
        dto.setTotalHours(totalHours);
        dto.setApprovedHours(approvedHours);
        dto.setProgressPercentageBySubjects(calculatePercentage(approvedSubjects, totalSubjects));
        dto.setProgressPercentageByCredits(calculatePercentage(approvedCredits, totalCredits));
        dto.setProgressPercentageByHours(calculatePercentage(approvedHours, totalHours));
        return dto;
    }

    public SubjectResponseDto getOwnedOrThrow(UUID subjectId, UUID userId) {
        return toResponseDto(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId));
    }

    public boolean ownsSubject(UUID subjectId, UUID userId) {
        requireSubjectId(subjectId);
        requireUserId(userId);
        return subjectCareerAccessService.userOwnsSubject(userId, subjectId);
    }

    public SubjectAvailabilityResponseDto getAvailability(UUID subjectId, UUID userId) {
        requireSubjectId(subjectId);
        requireUserId(userId);

        Subject subject = subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);
        Map<UUID, Subject> subjectsById = mapSubjectsById(subject.getCareerId());
        return toAvailabilityDto(subject, subjectsById, null, null);
    }

    public List<SubjectResponseDto> listUnlockedByStatusChange(UUID subjectId, UUID userId, String rawNewStatus) {
        requireSubjectId(subjectId);
        requireUserId(userId);
        String normalizedNewStatus = SubjectStatus.normalize(rawNewStatus);

        Subject changedSubject = subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);
        List<Subject> careerSubjects = subjectRepository.findByCareerId(changedSubject.getCareerId());
        Map<UUID, Subject> subjectsById = toSubjectMap(careerSubjects);

        return careerSubjects.stream()
                .filter(candidate -> !candidate.getId().equals(subjectId))
                .filter(candidate -> toCorrelativeList(candidate.getCorrelatives()).contains(subjectId))
                .filter(candidate -> {
                    boolean blockedBefore = isBlocked(candidate, subjectsById, null, null);
                    boolean blockedAfter = isBlocked(candidate, subjectsById, subjectId, normalizedNewStatus);
                    return blockedBefore && !blockedAfter;
                })
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public SubjectBasicDto getSubjectBasic(UUID subjectId) {
        requireSubjectId(subjectId);

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException("Materia no encontrada"));

        return new SubjectBasicDto(
                subject.getId(),
                subject.getCareerId(),
                subject.getName(),
                subject.getCode(),
                subject.getStatus());
    }

    @Transactional
    public SubjectResponseDto createSubject(UUID userId, SubjectCreateRequestDto request) {
        requireUserId(userId);
        ValidationUtils.requireNotNull(request, "El body de creacion es obligatorio");
        requireCareerId(request.getCareerId());

        subjectCareerAccessService.validateCareerOwnership(userId, request.getCareerId());

        String normalizedStatus = normalizeStatusOrDefault(request.getStatus());
        String[] correlatives = toCorrelativeArray(request.getCorrelatives());
        validateCorrelativeRulesForStatus(request.getCareerId(), correlatives, normalizedStatus);

        Subject subject = new Subject();
        subject.setCareerId(request.getCareerId());
        subject.setName(validateRequiredText(request.getName(), "nombre", MAX_TEXT_LENGTH));
        subject.setCode(validateOptionalText(request.getCode(), "codigo", MAX_CODE_LENGTH));
        subject.setStatus(normalizedStatus);
        subject.setGrade(validateIntegerGrade(request.getGrade(), "nota"));
        subject.setApprovalMethod(SubjectApprovalMethod.normalize(request.getApprovalMethod()));
        subject.setCorrelatives(correlatives);
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

        String requestedStatus = request.getStatus() != null ? SubjectStatus.normalize(request.getStatus()) : null;
        String effectiveStatus = requestedStatus != null ? requestedStatus : subject.getStatus();
        String[] effectiveCorrelatives = request.getCorrelatives() != null
                ? toCorrelativeArray(request.getCorrelatives())
                : subject.getCorrelatives();

        if (request.getStatus() != null || request.getCorrelatives() != null) {
            validateCorrelativeRulesForStatus(subject.getCareerId(), effectiveCorrelatives, effectiveStatus);
        }

        if (request.getName() != null) {
            subject.setName(validateRequiredText(request.getName(), "nombre", MAX_TEXT_LENGTH));
        }

        if (request.getCode() != null) {
            subject.setCode(validateOptionalText(request.getCode(), "codigo", MAX_CODE_LENGTH));
        }

        if (requestedStatus != null) {
            subject.setStatus(requestedStatus);
        }

        if (request.getGrade() != null) {
            subject.setGrade(validateIntegerGrade(request.getGrade(), "nota"));
        }

        if (request.getApprovalMethod() != null) {
            subject.setApprovalMethod(SubjectApprovalMethod.normalize(request.getApprovalMethod()));
        }

        if (request.getCorrelatives() != null) {
            subject.setCorrelatives(effectiveCorrelatives);
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

    private String normalizeSearchTerm(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        return normalized;
    }

    private String normalizeStatusFilter(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return null;
        }
        return SubjectStatus.normalize(rawStatus);
    }

    private Sort resolveSort(String rawSortBy, String rawSortDirection) {
        String sortBy = normalizeSearchTerm(rawSortBy);
        if (sortBy == null) {
            sortBy = "name";
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Campo de ordenamiento invalido");
        }

        Sort.Direction direction = resolveSortDirection(rawSortDirection);
        return Sort.by(direction, sortBy);
    }

    private Sort.Direction resolveSortDirection(String rawSortDirection) {
        String sortDirection = normalizeSearchTerm(rawSortDirection);
        if (sortDirection == null || "asc".equalsIgnoreCase(sortDirection)) {
            return Sort.Direction.ASC;
        }
        if ("desc".equalsIgnoreCase(sortDirection)) {
            return Sort.Direction.DESC;
        }
        throw new IllegalArgumentException("Direccion de ordenamiento invalida");
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

    private void validateCorrelativeRulesForStatus(UUID careerId, String[] correlatives, String normalizedStatus) {
        if (!requiresResolvedCorrelatives(normalizedStatus)) {
            return;
        }

        Subject subjectToValidate = new Subject();
        subjectToValidate.setCorrelatives(correlatives);

        boolean blocked = isBlocked(subjectToValidate, mapSubjectsById(careerId), null, null);
        if (!blocked) {
            return;
        }

        if (SubjectStatus.CURSANDO.getValue().equalsIgnoreCase(normalizedStatus)) {
            throw new BusinessException(
                    "La materia esta bloqueada por correlativas pendientes y no puede pasar a cursando");
        }

        if (SubjectStatus.APROBADA.getValue().equalsIgnoreCase(normalizedStatus)) {
            throw new BusinessException("No se puede aprobar una materia con correlativas pendientes");
        }
    }

    private boolean requiresResolvedCorrelatives(String normalizedStatus) {
        if (normalizedStatus == null || normalizedStatus.isBlank()) {
            return false;
        }

        String status = normalizedStatus.trim();
        return SubjectStatus.CURSANDO.getValue().equalsIgnoreCase(status)
                || SubjectStatus.APROBADA.getValue().equalsIgnoreCase(status);
    }

    private int countByStatus(List<Subject> subjects, SubjectStatus status) {
        return (int) subjects.stream()
                .map(Subject::getStatus)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(rawStatus -> status.getValue().equalsIgnoreCase(rawStatus))
                .count();
    }

    private int sumMetric(List<Subject> subjects, Function<Subject, Integer> extractor) {
        return subjects.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private int sumApprovedMetric(List<Subject> subjects, Function<Subject, Integer> extractor) {
        return subjects.stream()
                .filter(subject -> isApprovedStatus(subject.getStatus()))
                .map(extractor)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private double calculatePercentage(int numerator, int denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        double rawPercentage = (numerator * 100.0) / denominator;
        return Math.round(rawPercentage * 100.0) / 100.0;
    }

    private Map<UUID, Subject> toSubjectMap(List<Subject> subjects) {
        return subjects.stream()
                .collect(Collectors.toMap(Subject::getId, subject -> subject, (left, right) -> left));
    }

    private Map<UUID, Subject> mapSubjectsById(UUID careerId) {
        return toSubjectMap(subjectRepository.findByCareerId(careerId));
    }

    private SubjectAvailabilityResponseDto toAvailabilityDto(
            Subject subject,
            Map<UUID, Subject> subjectsById,
            UUID overrideSubjectId,
            String overrideStatus) {
        List<UUID> correlatives = toCorrelativeList(subject.getCorrelatives());
        List<UUID> missingCorrelatives = correlatives.stream()
                .filter(correlativeId -> !isCorrelativeApproved(
                        correlativeId,
                        subjectsById,
                        overrideSubjectId,
                        overrideStatus))
                .toList();

        SubjectAvailabilityResponseDto dto = new SubjectAvailabilityResponseDto();
        dto.setSubjectId(subject.getId());
        dto.setCorrelatives(correlatives);
        dto.setMissingCorrelatives(missingCorrelatives);
        dto.setBlocked(!missingCorrelatives.isEmpty());
        dto.setAvailable(missingCorrelatives.isEmpty());
        return dto;
    }

    private boolean isBlocked(
            Subject subject,
            Map<UUID, Subject> subjectsById,
            UUID overrideSubjectId,
            String overrideStatus) {
        return toCorrelativeList(subject.getCorrelatives()).stream()
                .anyMatch(correlativeId -> !isCorrelativeApproved(
                        correlativeId,
                        subjectsById,
                        overrideSubjectId,
                        overrideStatus));
    }

    private boolean isCorrelativeApproved(
            UUID correlativeId,
            Map<UUID, Subject> subjectsById,
            UUID overrideSubjectId,
            String overrideStatus) {
        if (overrideSubjectId != null && overrideSubjectId.equals(correlativeId)) {
            return isApprovedStatus(overrideStatus);
        }

        Subject correlative = subjectsById.get(correlativeId);
        return correlative != null && isApprovedStatus(correlative.getStatus());
    }

    private boolean isApprovedStatus(String status) {
        if (status == null || status.isBlank()) {
            return false;
        }
        return SubjectStatus.APROBADA.getValue().equalsIgnoreCase(status.trim());
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
