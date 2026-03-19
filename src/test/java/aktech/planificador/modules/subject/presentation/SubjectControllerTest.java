package aktech.planificador.modules.subject.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import aktech.planificador.modules.subject.dto.CareerProgressResponseDto;
import aktech.planificador.modules.subject.application.SubjectDashboardService;
import aktech.planificador.modules.subject.application.SubjectService;
import aktech.planificador.modules.subject.dto.CareerDashboardResponseDto;
import aktech.planificador.modules.subject.dto.SubjectAvailabilityResponseDto;
import aktech.planificador.modules.subject.dto.SubjectCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectResponseDto;
import aktech.planificador.modules.subject.dto.SubjectUpdateRequestDto;
import aktech.planificador.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class SubjectControllerTest {

    @Mock
    private SubjectService subjectService;

    @Mock
    private SubjectDashboardService subjectDashboardService;

    private SubjectController subjectController;

    @BeforeEach
    void setUp() {
        subjectController = new SubjectController(subjectService, subjectDashboardService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createSubject_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectCreateRequestDto request = new SubjectCreateRequestDto();
        SubjectResponseDto expected = new SubjectResponseDto();

        when(subjectService.createSubject(userId, request)).thenReturn(expected);

        SubjectResponseDto response = subjectController.createSubject(request);

        assertSame(expected, response);
        verify(subjectService).createSubject(userId, request);
    }

    @Test
    void listByCareer_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<SubjectResponseDto> expected = List.of(new SubjectResponseDto());
        when(subjectService.listByCareer(userId, careerId)).thenReturn(expected);

        List<SubjectResponseDto> response = subjectController.listByCareer(careerId);

        assertSame(expected, response);
        verify(subjectService).listByCareer(userId, careerId);
    }

    @Test
    void listByCareerAndStatus_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<SubjectResponseDto> expected = List.of(new SubjectResponseDto());
        when(subjectService.listByCareerAndStatus(userId, careerId, "cursando")).thenReturn(expected);

        List<SubjectResponseDto> response = subjectController.listByCareerAndStatus(careerId, "cursando");

        assertSame(expected, response);
        verify(subjectService).listByCareerAndStatus(userId, careerId, "cursando");
    }

    @Test
    void searchByCareer_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<SubjectResponseDto> expected = List.of(new SubjectResponseDto());
        when(subjectService.listByCareerWithFilters(userId, careerId, "Analisis", "MAT", "cursando", 2, 1, "year",
                "desc"))
                .thenReturn(expected);

        List<SubjectResponseDto> response = subjectController.searchByCareer(
                careerId,
                "Analisis",
                "MAT",
                "cursando",
                2,
                1,
                "year",
                "desc");

        assertSame(expected, response);
        verify(subjectService).listByCareerWithFilters(userId, careerId, "Analisis", "MAT", "cursando", 2, 1,
                "year", "desc");
    }

    @Test
    void getCareerProgress_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        CareerProgressResponseDto expected = new CareerProgressResponseDto();
        expected.setCareerId(careerId);
        expected.setProgressPercentageBySubjects(50.0);

        when(subjectService.getCareerProgress(userId, careerId)).thenReturn(expected);

        CareerProgressResponseDto response = subjectController.getCareerProgress(careerId);

        assertSame(expected, response);
        verify(subjectService).getCareerProgress(userId, careerId);
    }

    @Test
    void getCareerDashboard_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        CareerDashboardResponseDto expected = new CareerDashboardResponseDto();
        expected.setCareerId(careerId);
        expected.setActiveSubjects(2);

        when(subjectDashboardService.getCareerDashboard(userId, careerId)).thenReturn(expected);

        CareerDashboardResponseDto response = subjectController.getCareerDashboard(careerId);

        assertSame(expected, response);
        verify(subjectDashboardService).getCareerDashboard(userId, careerId);
    }

    @Test
    void getByIdOwned_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectResponseDto expected = new SubjectResponseDto();
        when(subjectService.getOwnedOrThrow(subjectId, userId)).thenReturn(expected);

        SubjectResponseDto response = subjectController.getByIdOwned(subjectId);

        assertSame(expected, response);
        verify(subjectService).getOwnedOrThrow(subjectId, userId);
    }

    @Test
    void updateSubject_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectUpdateRequestDto request = new SubjectUpdateRequestDto();
        SubjectResponseDto expected = new SubjectResponseDto();

        when(subjectService.updateSubject(subjectId, userId, request)).thenReturn(expected);

        SubjectResponseDto response = subjectController.updateSubject(subjectId, request);

        assertSame(expected, response);
        verify(subjectService).updateSubject(subjectId, userId, request);
    }

    @Test
    void deleteSubject_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        subjectController.deleteSubject(subjectId);

        verify(subjectService).deleteSubject(subjectId, userId);
    }

    @Test
    void ownsSubject_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        when(subjectService.ownsSubject(subjectId, userId)).thenReturn(true);

        boolean response = subjectController.ownsSubject(subjectId);

        assertEquals(true, response);
        verify(subjectService).ownsSubject(subjectId, userId);
    }

    @Test
    void getAvailability_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectAvailabilityResponseDto expected = new SubjectAvailabilityResponseDto();
        expected.setSubjectId(subjectId);
        expected.setBlocked(true);

        when(subjectService.getAvailability(subjectId, userId)).thenReturn(expected);

        SubjectAvailabilityResponseDto response = subjectController.getAvailability(subjectId);

        assertSame(expected, response);
        verify(subjectService).getAvailability(subjectId, userId);
    }

    @Test
    void listUnlockedByStatusChange_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<SubjectResponseDto> expected = List.of(new SubjectResponseDto());
        when(subjectService.listUnlockedByStatusChange(subjectId, userId, "aprobada")).thenReturn(expected);

        List<SubjectResponseDto> response = subjectController.listUnlockedByStatusChange(subjectId, "aprobada");

        assertSame(expected, response);
        verify(subjectService).listUnlockedByStatusChange(subjectId, userId, "aprobada");
    }

    @Test
    void listByCareer_shouldThrowWhenNoAuthenticatedUser() {
        SecurityContextHolder.clearContext();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> subjectController.listByCareer(UUID.randomUUID()));

        assertEquals("No hay usuario autenticado", ex.getMessage());
    }

    @Test
    void ownsSubject_shouldThrowWhenTokenUserIdIsNotUuid() {
        setAuthenticatedUser("not-a-uuid");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> subjectController.ownsSubject(UUID.randomUUID()));

        assertEquals("Token invalido: userId no es UUID", ex.getMessage());
    }

    private void setAuthenticatedUser(String principal) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
