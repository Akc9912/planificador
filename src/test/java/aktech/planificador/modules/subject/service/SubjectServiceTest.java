package aktech.planificador.modules.subject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import aktech.planificador.modules.subject.dto.CareerProgressResponseDto;
import aktech.planificador.modules.subject.dto.SubjectCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectAvailabilityResponseDto;
import aktech.planificador.modules.subject.dto.SubjectResponseDto;
import aktech.planificador.modules.subject.dto.SubjectUpdateRequestDto;
import aktech.planificador.modules.subject.model.Subject;
import aktech.planificador.modules.subject.repository.SubjectRepository;
import aktech.planificador.shared.event.SubjectStatusChangedEvent;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SubjectCareerAccessService subjectCareerAccessService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private SubjectService subjectService;

    @BeforeEach
    void setUp() {
        subjectService = new SubjectService(subjectRepository, subjectCareerAccessService, eventPublisher);
    }

    @Test
    void createSubject_shouldCreateWithDefaultsAndValidatedOwnership() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        SubjectCreateRequestDto request = new SubjectCreateRequestDto();
        request.setCareerId(careerId);
        request.setName("  Algebra  ");

        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> {
            Subject saved = invocation.getArgument(0);
            saved.setId(subjectId);
            return saved;
        });

        SubjectResponseDto response = subjectService.createSubject(userId, request);

        verify(subjectCareerAccessService).validateCareerOwnership(userId, careerId);

        ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
        verify(subjectRepository).save(subjectCaptor.capture());

        Subject saved = subjectCaptor.getValue();
        assertEquals(careerId, saved.getCareerId());
        assertEquals("Algebra", saved.getName());
        assertEquals("pendiente", saved.getStatus());
        assertEquals("#3B82F6", saved.getColor());
        assertFalse(saved.isEntranceCourse());
        assertEquals(0, saved.getCorrelatives().length);

        assertEquals(subjectId, response.getId());
        assertEquals("Algebra", response.getName());
        assertEquals("pendiente", response.getStatus());
    }

    @Test
    void updateSubject_shouldPublishStatusChangedEventWhenStatusChanges() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();

        Subject existing = createSubject(subjectId, careerId, "Historia", "pendiente");
        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId)).thenReturn(existing);
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SubjectUpdateRequestDto request = new SubjectUpdateRequestDto();
        request.setStatus("cursando");

        SubjectResponseDto response = subjectService.updateSubject(subjectId, userId, request);

        assertEquals("cursando", response.getStatus());

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        Object published = eventCaptor.getValue();
        assertInstanceOf(SubjectStatusChangedEvent.class, published);

        SubjectStatusChangedEvent event = (SubjectStatusChangedEvent) published;
        assertEquals(subjectId, event.getSubjectId());
        assertEquals(careerId, event.getCareerId());
        assertEquals("pendiente", event.getOldStatus());
        assertEquals("cursando", event.getNewStatus());
    }

    @Test
    void listByCareer_shouldValidateCareerOwnership() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();

        Subject subject = createSubject(UUID.randomUUID(), careerId, "Fisica", "pendiente");
        when(subjectRepository.findByCareerId(careerId)).thenReturn(List.of(subject));

        List<SubjectResponseDto> result = subjectService.listByCareer(userId, careerId);

        verify(subjectCareerAccessService).validateCareerOwnership(userId, careerId);
        assertEquals(1, result.size());
        assertEquals("Fisica", result.get(0).getName());
    }

    @Test
    void getCareerProgress_shouldCalculateCountsPercentagesAndDerivedStats() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();

        UUID approvedId = UUID.randomUUID();
        UUID inProgressId = UUID.randomUUID();
        UUID availablePendingId = UUID.randomUUID();
        UUID blockedPendingId = UUID.randomUUID();

        Subject approved = createSubject(approvedId, careerId, "Analisis I", "aprobada");
        approved.setCredits(10);
        approved.setHours(100);

        Subject inProgress = createSubject(inProgressId, careerId, "Fisica I", "cursando");
        inProgress.setCredits(6);
        inProgress.setHours(80);

        Subject availablePending = createSubject(availablePendingId, careerId, "Analisis II", "pendiente");
        availablePending.setCredits(8);
        availablePending.setHours(90);
        availablePending.setCorrelatives(new String[] { approvedId.toString() });

        Subject blockedPending = createSubject(blockedPendingId, careerId, "Fisica II", "pendiente");
        blockedPending.setCredits(4);
        blockedPending.setHours(70);
        blockedPending.setCorrelatives(new String[] { inProgressId.toString() });

        when(subjectRepository.findByCareerId(careerId))
                .thenReturn(List.of(approved, inProgress, availablePending, blockedPending));

        CareerProgressResponseDto response = subjectService.getCareerProgress(userId, careerId);

        verify(subjectCareerAccessService).validateCareerOwnership(userId, careerId);
        assertEquals(careerId, response.getCareerId());
        assertEquals(4, response.getTotalSubjects());
        assertEquals(1, response.getApprovedSubjects());
        assertEquals(2, response.getPendingSubjects());
        assertEquals(1, response.getInProgressSubjects());
        assertEquals(0, response.getRegularSubjects());
        assertEquals(0, response.getLibreSubjects());
        assertEquals(1, response.getBlockedSubjects());
        assertEquals(2, response.getAvailableSubjects());

        assertEquals(28, response.getTotalCredits());
        assertEquals(10, response.getApprovedCredits());
        assertEquals(340, response.getTotalHours());
        assertEquals(100, response.getApprovedHours());

        assertEquals(25.0, response.getProgressPercentageBySubjects());
        assertEquals(35.71, response.getProgressPercentageByCredits());
        assertEquals(29.41, response.getProgressPercentageByHours());
    }

    @Test
    void getCareerProgress_shouldReturnZeroPercentagesWhenCareerHasNoSubjects() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();

        when(subjectRepository.findByCareerId(careerId)).thenReturn(List.of());

        CareerProgressResponseDto response = subjectService.getCareerProgress(userId, careerId);

        verify(subjectCareerAccessService).validateCareerOwnership(userId, careerId);
        assertEquals(0, response.getTotalSubjects());
        assertEquals(0, response.getProgressPercentageBySubjects());
        assertEquals(0, response.getProgressPercentageByCredits());
        assertEquals(0, response.getProgressPercentageByHours());
        assertEquals(0, response.getAvailableSubjects());
        assertEquals(0, response.getBlockedSubjects());
    }

    @Test
    void userOwnsSubject_shouldDelegateToAccessService() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        when(subjectCareerAccessService.userOwnsSubject(userId, subjectId)).thenReturn(true);

        boolean result = subjectService.userOwnsSubject(userId, subjectId);

        assertTrue(result);
        verify(subjectCareerAccessService).userOwnsSubject(userId, subjectId);
    }

    @Test
    void getAvailability_shouldReturnBlockedWhenAnyCorrelativeIsNotApproved() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID approvedCorrelativeId = UUID.randomUUID();
        UUID pendingCorrelativeId = UUID.randomUUID();

        Subject subject = createSubject(subjectId, careerId, "Analisis II", "pendiente");
        subject.setCorrelatives(new String[] { approvedCorrelativeId.toString(), pendingCorrelativeId.toString() });

        Subject approvedCorrelative = createSubject(approvedCorrelativeId, careerId, "Algebra", "aprobada");
        Subject pendingCorrelative = createSubject(pendingCorrelativeId, careerId, "Analisis I", "cursando");

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId)).thenReturn(subject);
        when(subjectRepository.findByCareerId(careerId))
                .thenReturn(List.of(subject, approvedCorrelative, pendingCorrelative));

        SubjectAvailabilityResponseDto response = subjectService.getAvailability(subjectId, userId);

        assertTrue(response.isBlocked());
        assertFalse(response.isAvailable());
        assertEquals(List.of(approvedCorrelativeId, pendingCorrelativeId), response.getCorrelatives());
        assertEquals(List.of(pendingCorrelativeId), response.getMissingCorrelatives());
    }

    @Test
    void getAvailability_shouldReturnAvailableWhenSubjectHasNoCorrelatives() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        Subject subject = createSubject(subjectId, careerId, "Historia", "pendiente");

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId)).thenReturn(subject);
        when(subjectRepository.findByCareerId(careerId)).thenReturn(List.of(subject));

        SubjectAvailabilityResponseDto response = subjectService.getAvailability(subjectId, userId);

        assertFalse(response.isBlocked());
        assertTrue(response.isAvailable());
        assertEquals(List.of(), response.getCorrelatives());
        assertEquals(List.of(), response.getMissingCorrelatives());
    }

    @Test
    void getAvailability_shouldTreatMissingCorrelativeAsBlockedEdgeCase() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID missingCorrelativeId = UUID.randomUUID();

        Subject subject = createSubject(subjectId, careerId, "Fisica II", "pendiente");
        subject.setCorrelatives(new String[] { missingCorrelativeId.toString() });

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId)).thenReturn(subject);
        when(subjectRepository.findByCareerId(careerId)).thenReturn(List.of(subject));

        SubjectAvailabilityResponseDto response = subjectService.getAvailability(subjectId, userId);

        assertTrue(response.isBlocked());
        assertFalse(response.isAvailable());
        assertEquals(List.of(missingCorrelativeId), response.getMissingCorrelatives());
    }

    @Test
    void listUnlockedByStatusChange_shouldReturnSubjectsThatBecomeAvailable() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();

        UUID changedSubjectId = UUID.randomUUID();
        UUID unlockedSubjectId = UUID.randomUUID();
        UUID stillBlockedSubjectId = UUID.randomUUID();
        UUID approvedCorrelativeId = UUID.randomUUID();
        UUID pendingCorrelativeId = UUID.randomUUID();

        Subject changedSubject = createSubject(changedSubjectId, careerId, "Analisis I", "cursando");

        Subject unlockedSubject = createSubject(unlockedSubjectId, careerId, "Analisis II", "pendiente");
        unlockedSubject.setCorrelatives(new String[] { changedSubjectId.toString(), approvedCorrelativeId.toString() });

        Subject stillBlockedSubject = createSubject(stillBlockedSubjectId, careerId, "Fisica II", "pendiente");
        stillBlockedSubject
                .setCorrelatives(new String[] { changedSubjectId.toString(), pendingCorrelativeId.toString() });

        Subject approvedCorrelative = createSubject(approvedCorrelativeId, careerId, "Algebra", "aprobada");
        Subject pendingCorrelative = createSubject(pendingCorrelativeId, careerId, "Fisica I", "cursando");

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, changedSubjectId)).thenReturn(changedSubject);
        when(subjectRepository.findByCareerId(careerId)).thenReturn(List.of(
                changedSubject,
                unlockedSubject,
                stillBlockedSubject,
                approvedCorrelative,
                pendingCorrelative));

        List<SubjectResponseDto> response = subjectService.listUnlockedByStatusChange(
                changedSubjectId,
                userId,
                "aprobada");

        assertEquals(1, response.size());
        assertEquals(unlockedSubjectId, response.get(0).getId());
    }

    @Test
    void listUnlockedByStatusChange_shouldReturnEmptyWhenNewStatusDoesNotUnlock() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        UUID changedSubjectId = UUID.randomUUID();
        UUID dependentSubjectId = UUID.randomUUID();

        Subject changedSubject = createSubject(changedSubjectId, careerId, "Programacion I", "pendiente");

        Subject dependentSubject = createSubject(dependentSubjectId, careerId, "Programacion II", "pendiente");
        dependentSubject.setCorrelatives(new String[] { changedSubjectId.toString() });

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, changedSubjectId)).thenReturn(changedSubject);
        when(subjectRepository.findByCareerId(careerId)).thenReturn(List.of(changedSubject, dependentSubject));

        List<SubjectResponseDto> response = subjectService.listUnlockedByStatusChange(
                changedSubjectId,
                userId,
                "cursando");

        assertTrue(response.isEmpty());
    }

    @Test
    void createSubject_shouldThrowWhenCareerIdIsMissing() {
        SubjectCreateRequestDto request = new SubjectCreateRequestDto();
        request.setName("Analisis");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> subjectService.createSubject(UUID.randomUUID(), request));

        assertEquals("El id de carrera es obligatorio", ex.getMessage());
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
