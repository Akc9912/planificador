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

import aktech.planificador.modules.subject.dto.SubjectCreateRequestDto;
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
    void userOwnsSubject_shouldDelegateToAccessService() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        when(subjectCareerAccessService.userOwnsSubject(userId, subjectId)).thenReturn(true);

        boolean result = subjectService.userOwnsSubject(userId, subjectId);

        assertTrue(result);
        verify(subjectCareerAccessService).userOwnsSubject(userId, subjectId);
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
