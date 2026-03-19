package aktech.planificador.modules.subject.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aktech.planificador.modules.subject.dto.CareerDashboardResponseDto;
import aktech.planificador.modules.subject.dto.CareerProgressResponseDto;
import aktech.planificador.modules.subject.dto.SubjectDashboardItemDto;
import aktech.planificador.modules.subject.enums.SubjectStatus;
import aktech.planificador.modules.subject.model.Subject;
import aktech.planificador.modules.subject.repository.SubjectRepository;
import aktech.planificador.shared.util.ValidationUtils;

@Service
@Transactional(readOnly = true)
public class SubjectDashboardService {

    private final SubjectRepository subjectRepository;
    private final SubjectCareerAccessService subjectCareerAccessService;
    private final SubjectService subjectService;

    public SubjectDashboardService(
            SubjectRepository subjectRepository,
            SubjectCareerAccessService subjectCareerAccessService,
            SubjectService subjectService) {
        this.subjectRepository = subjectRepository;
        this.subjectCareerAccessService = subjectCareerAccessService;
        this.subjectService = subjectService;
    }

    public CareerDashboardResponseDto getCareerDashboard(UUID userId, UUID careerId) {
        requireUserId(userId);
        requireCareerId(careerId);
        subjectCareerAccessService.validateCareerOwnership(userId, careerId);

        List<Subject> careerSubjects = subjectRepository.findByCareerId(careerId);
        Map<UUID, Subject> subjectsById = toSubjectMap(careerSubjects);

        List<Subject> currentSubjects = careerSubjects.stream()
                .filter(subject -> SubjectStatus.CURSANDO.getValue().equalsIgnoreCase(subject.getStatus()))
                .sorted(dashboardSort())
                .toList();

        List<Subject> recommendedSubjects = careerSubjects.stream()
                .filter(subject -> SubjectStatus.PENDIENTE.getValue().equalsIgnoreCase(subject.getStatus()))
                .filter(subject -> isAvailable(subject, subjectsById))
                .sorted(dashboardSort())
                .toList();

        CareerProgressResponseDto progress = subjectService.getCareerProgress(userId, careerId);
        int weeklyHours = currentSubjects.stream()
                .map(Subject::getHours)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        CareerDashboardResponseDto dto = new CareerDashboardResponseDto();
        dto.setCareerId(careerId);
        dto.setCurrentSubjects(currentSubjects.stream().map(this::toDashboardItem).toList());
        dto.setRecommendedSubjects(recommendedSubjects.stream().map(this::toDashboardItem).toList());
        dto.setProgress(progress);
        dto.setActiveSubjects(currentSubjects.size());
        dto.setWeeklyHours(weeklyHours);
        dto.setAlerts(buildAlerts(progress, currentSubjects.size(), recommendedSubjects.size()));
        return dto;
    }

    private SubjectDashboardItemDto toDashboardItem(Subject subject) {
        SubjectDashboardItemDto dto = new SubjectDashboardItemDto();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setCode(subject.getCode());
        dto.setStatus(subject.getStatus());
        dto.setYear(subject.getYear());
        dto.setSemester(subject.getSemester());
        dto.setHours(subject.getHours());
        dto.setCredits(subject.getCredits());
        return dto;
    }

    private List<String> buildAlerts(CareerProgressResponseDto progress, int activeSubjects, int recommendedSubjects) {
        List<String> alerts = new ArrayList<>();

        if (progress != null && progress.getTotalSubjects() == 0) {
            alerts.add("La carrera no tiene materias cargadas");
        }

        if (activeSubjects == 0) {
            alerts.add("No hay materias activas actualmente");
        }

        if (progress != null && progress.getBlockedSubjects() > 0) {
            alerts.add("Tenes " + progress.getBlockedSubjects() + " materias bloqueadas por correlativas pendientes");
        }

        if (progress != null && progress.getPendingSubjects() > 0 && recommendedSubjects == 0) {
            alerts.add("No hay materias pendientes habilitadas para cursar");
        }

        return alerts;
    }

    private boolean isAvailable(Subject subject, Map<UUID, Subject> subjectsById) {
        return toCorrelativeList(subject.getCorrelatives()).stream()
                .allMatch(correlativeId -> isCorrelativeApproved(correlativeId, subjectsById));
    }

    private boolean isCorrelativeApproved(UUID correlativeId, Map<UUID, Subject> subjectsById) {
        Subject correlative = subjectsById.get(correlativeId);
        return correlative != null && SubjectStatus.APROBADA.getValue().equalsIgnoreCase(correlative.getStatus());
    }

    private Map<UUID, Subject> toSubjectMap(List<Subject> subjects) {
        return subjects.stream()
                .collect(Collectors.toMap(Subject::getId, subject -> subject, (left, right) -> left));
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

    private Comparator<Subject> dashboardSort() {
        return Comparator
                .comparing((Subject subject) -> sortableInteger(subject.getYear()))
                .thenComparing(subject -> sortableInteger(subject.getSemester()))
                .thenComparing(subject -> sortableString(subject.getName()), String.CASE_INSENSITIVE_ORDER);
    }

    private int sortableInteger(Integer value) {
        return value == null ? Integer.MAX_VALUE : value;
    }

    private String sortableString(String value) {
        return value == null ? "" : value;
    }

    private void requireUserId(UUID userId) {
        ValidationUtils.requireNotNull(userId, "El id de usuario es obligatorio");
    }

    private void requireCareerId(UUID careerId) {
        ValidationUtils.requireNotNull(careerId, "El id de carrera es obligatorio");
    }
}
