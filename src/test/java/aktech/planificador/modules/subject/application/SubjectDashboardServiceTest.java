package aktech.planificador.modules.subject.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import aktech.planificador.modules.subject.domain.model.Subject;
import aktech.planificador.modules.subject.dto.CareerDashboardResponseDto;
import aktech.planificador.modules.subject.dto.CareerProgressResponseDto;
import aktech.planificador.modules.subject.persistence.SubjectRepository;

@ExtendWith(MockitoExtension.class)
class SubjectDashboardServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SubjectCareerAccessService subjectCareerAccessService;

    @Mock
    private SubjectService subjectService;

    private SubjectDashboardService subjectDashboardService;

    @BeforeEach
    void setUp() {
        subjectDashboardService = new SubjectDashboardService(subjectRepository, subjectCareerAccessService,
                subjectService);
    }

    @Test
    void getCareerDashboard_shouldAggregateCurrentRecommendedProgressAndAlerts() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();

        UUID approvedCorrelativeId = UUID.randomUUID();
        UUID pendingCorrelativeId = UUID.randomUUID();

        Subject current = createSubject(UUID.randomUUID(), careerId, "Fisica I", "cursando");
        current.setHours(8);
        current.setYear(1);
        current.setSemester(1);

        Subject approvedCorrelative = createSubject(approvedCorrelativeId, careerId, "Algebra", "aprobada");

        Subject recommended = createSubject(UUID.randomUUID(), careerId, "Fisica II", "pendiente");
        recommended.setCorrelatives(new String[] { approvedCorrelativeId.toString() });
        recommended.setYear(2);
        recommended.setSemester(1);

        Subject blocked = createSubject(UUID.randomUUID(), careerId, "Analisis II", "pendiente");
        blocked.setCorrelatives(new String[] { pendingCorrelativeId.toString() });

        Subject pendingCorrelative = createSubject(pendingCorrelativeId, careerId, "Analisis I", "regular");

        when(subjectRepository.findByCareerId(careerId))
                .thenReturn(List.of(current, approvedCorrelative, recommended, blocked, pendingCorrelative));

        CareerProgressResponseDto progress = new CareerProgressResponseDto();
        progress.setCareerId(careerId);
        progress.setTotalSubjects(5);
        progress.setPendingSubjects(2);
        progress.setBlockedSubjects(1);
        when(subjectService.getCareerProgress(userId, careerId)).thenReturn(progress);

        CareerDashboardResponseDto response = subjectDashboardService.getCareerDashboard(userId, careerId);

        verify(subjectCareerAccessService).validateCareerOwnership(userId, careerId);
        verify(subjectService).getCareerProgress(userId, careerId);

        assertEquals(careerId, response.getCareerId());
        assertEquals(1, response.getCurrentSubjects().size());
        assertEquals(current.getId(), response.getCurrentSubjects().get(0).getId());
        assertEquals(1, response.getRecommendedSubjects().size());
        assertEquals(recommended.getId(), response.getRecommendedSubjects().get(0).getId());
        assertEquals(1, response.getActiveSubjects());
        assertEquals(8, response.getWeeklyHours());
        assertTrue(response.getAlerts().contains("Tenes 1 materias bloqueadas por correlativas pendientes"));
    }

    @Test
    void getCareerDashboard_shouldReturnAlertsForEmptyCareer() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();

        when(subjectRepository.findByCareerId(careerId)).thenReturn(List.of());

        CareerProgressResponseDto progress = new CareerProgressResponseDto();
        progress.setCareerId(careerId);
        progress.setTotalSubjects(0);
        progress.setPendingSubjects(0);
        progress.setBlockedSubjects(0);
        when(subjectService.getCareerProgress(userId, careerId)).thenReturn(progress);

        CareerDashboardResponseDto response = subjectDashboardService.getCareerDashboard(userId, careerId);

        assertEquals(0, response.getCurrentSubjects().size());
        assertEquals(0, response.getRecommendedSubjects().size());
        assertEquals(0, response.getActiveSubjects());
        assertEquals(0, response.getWeeklyHours());
        assertTrue(response.getAlerts().contains("La carrera no tiene materias cargadas"));
        assertTrue(response.getAlerts().contains("No hay materias activas actualmente"));
    }

    private Subject createSubject(UUID subjectId, UUID careerId, String name, String status) {
        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setCareerId(careerId);
        subject.setName(name);
        subject.setStatus(status);
        return subject;
    }
}
