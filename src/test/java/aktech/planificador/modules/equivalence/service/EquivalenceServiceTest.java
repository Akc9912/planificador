package aktech.planificador.modules.equivalence.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import aktech.planificador.modules.equivalence.dto.EquivalenceCreateRequestDto;
import aktech.planificador.modules.equivalence.dto.EquivalenceResponseDto;
import aktech.planificador.modules.equivalence.dto.EquivalenceUpdateRequestDto;
import aktech.planificador.modules.equivalence.model.Equivalence;
import aktech.planificador.modules.equivalence.repository.EquivalenceRepository;
import aktech.planificador.shared.api.SubjectApi;
import aktech.planificador.shared.dto.SubjectBasicDto;
import aktech.planificador.shared.exception.BusinessException;
import aktech.planificador.shared.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class EquivalenceServiceTest {

    @Mock
    private EquivalenceRepository equivalenceRepository;

    @Mock
    private SubjectApi subjectApi;

    private EquivalenceService equivalenceService;

    @BeforeEach
    void setUp() {
        equivalenceService = new EquivalenceService(equivalenceRepository, subjectApi);
    }

    @Test
    void createEquivalence_shouldPersistWhenValid() {
        UUID userId = UUID.randomUUID();
        UUID subjectAId = UUID.randomUUID();
        UUID subjectBId = UUID.randomUUID();
        UUID equivalenceId = UUID.randomUUID();

        EquivalenceCreateRequestDto request = new EquivalenceCreateRequestDto();
        request.setType("total");
        request.setSubjectAId(subjectAId);
        request.setSubjectBId(subjectBId);
        request.setNotes("  Equivalencia aceptada  ");

        mockOwnedSubjects(userId, subjectAId, subjectBId);
        mockDifferentCareers(subjectAId, subjectBId);
        mockNoDuplicatesOnCreate(userId, subjectAId, subjectBId);

        when(equivalenceRepository.save(any(Equivalence.class))).thenAnswer(invocation -> {
            Equivalence saved = invocation.getArgument(0);
            saved.setId(equivalenceId);
            return saved;
        });

        EquivalenceResponseDto response = equivalenceService.createEquivalence(userId, request);

        ArgumentCaptor<Equivalence> captor = ArgumentCaptor.forClass(Equivalence.class);
        verify(equivalenceRepository).save(captor.capture());

        Equivalence saved = captor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals(subjectAId, saved.getSubjectAId());
        assertEquals(subjectBId, saved.getSubjectBId());
        assertEquals("total", saved.getType());
        assertEquals("Equivalencia aceptada", saved.getNotes());

        assertEquals(equivalenceId, response.getId());
        assertEquals("total", response.getType());
    }

    @Test
    void createEquivalence_shouldThrowWhenCircular() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        EquivalenceCreateRequestDto request = new EquivalenceCreateRequestDto();
        request.setType("parcial");
        request.setSubjectAId(subjectId);
        request.setSubjectBId(subjectId);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> equivalenceService.createEquivalence(userId, request));

        assertEquals("Una materia no puede ser equivalente a si misma", ex.getMessage());
    }

    @Test
    void createEquivalence_shouldThrowWhenReversePairAlreadyExists() {
        UUID userId = UUID.randomUUID();
        UUID subjectAId = UUID.randomUUID();
        UUID subjectBId = UUID.randomUUID();

        EquivalenceCreateRequestDto request = new EquivalenceCreateRequestDto();
        request.setType("total");
        request.setSubjectAId(subjectAId);
        request.setSubjectBId(subjectBId);

        mockOwnedSubjects(userId, subjectAId, subjectBId);
        mockDifferentCareers(subjectAId, subjectBId);

        when(equivalenceRepository.existsByUserIdAndSubjectAIdAndSubjectBId(userId, subjectAId, subjectBId))
                .thenReturn(false);
        when(equivalenceRepository.existsByUserIdAndSubjectAIdAndSubjectBId(userId, subjectBId, subjectAId))
                .thenReturn(true);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> equivalenceService.createEquivalence(userId, request));

        assertEquals("Ya existe una equivalencia entre las materias indicadas", ex.getMessage());
    }

    @Test
    void createEquivalence_shouldThrowWhenSubjectsBelongToSameCareer() {
        UUID userId = UUID.randomUUID();
        UUID subjectAId = UUID.randomUUID();
        UUID subjectBId = UUID.randomUUID();
        UUID sameCareerId = UUID.randomUUID();

        EquivalenceCreateRequestDto request = new EquivalenceCreateRequestDto();
        request.setType("total");
        request.setSubjectAId(subjectAId);
        request.setSubjectBId(subjectBId);

        mockOwnedSubjects(userId, subjectAId, subjectBId);

        when(subjectApi.getSubjectBasic(subjectAId))
                .thenReturn(new SubjectBasicDto(subjectAId, sameCareerId, "Algebra", "ALG", "aprobada"));
        when(subjectApi.getSubjectBasic(subjectBId))
                .thenReturn(new SubjectBasicDto(subjectBId, sameCareerId, "Fisica", "FIS", "aprobada"));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> equivalenceService.createEquivalence(userId, request));

        assertEquals("Las equivalencias deben vincular materias de carreras distintas", ex.getMessage());
    }

    @Test
    void updateEquivalence_shouldUpdateUsingValidatedState() {
        UUID userId = UUID.randomUUID();
        UUID equivalenceId = UUID.randomUUID();
        UUID subjectAId = UUID.randomUUID();
        UUID subjectBId = UUID.randomUUID();

        Equivalence existing = new Equivalence();
        existing.setId(equivalenceId);
        existing.setUserId(userId);
        existing.setType("total");
        existing.setSubjectAId(subjectAId);
        existing.setSubjectBId(subjectBId);
        existing.setNotes("Notas iniciales");

        when(equivalenceRepository.findByIdAndUserId(equivalenceId, userId)).thenReturn(Optional.of(existing));

        EquivalenceUpdateRequestDto request = new EquivalenceUpdateRequestDto();
        request.setType("parcial");
        request.setNotes("  Ajuste actualizado  ");

        mockOwnedSubjects(userId, subjectAId, subjectBId);
        mockDifferentCareers(subjectAId, subjectBId);

        when(equivalenceRepository.existsByUserIdAndSubjectAIdAndSubjectBIdAndIdNot(userId, subjectAId, subjectBId,
                equivalenceId)).thenReturn(false);
        when(equivalenceRepository.existsByUserIdAndSubjectAIdAndSubjectBIdAndIdNot(userId, subjectBId, subjectAId,
                equivalenceId)).thenReturn(false);

        when(equivalenceRepository.save(any(Equivalence.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EquivalenceResponseDto response = equivalenceService.updateEquivalence(equivalenceId, userId, request);

        assertEquals("parcial", response.getType());
        assertEquals("Ajuste actualizado", response.getNotes());
    }

    @Test
    void updateEquivalence_shouldThrowWhenCircularAfterUpdate() {
        UUID userId = UUID.randomUUID();
        UUID equivalenceId = UUID.randomUUID();
        UUID subjectAId = UUID.randomUUID();
        UUID subjectBId = UUID.randomUUID();

        Equivalence existing = new Equivalence();
        existing.setId(equivalenceId);
        existing.setUserId(userId);
        existing.setType("total");
        existing.setSubjectAId(subjectAId);
        existing.setSubjectBId(subjectBId);

        when(equivalenceRepository.findByIdAndUserId(equivalenceId, userId)).thenReturn(Optional.of(existing));

        EquivalenceUpdateRequestDto request = new EquivalenceUpdateRequestDto();
        request.setSubjectBId(subjectAId);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> equivalenceService.updateEquivalence(equivalenceId, userId, request));

        assertEquals("Una materia no puede ser equivalente a si misma", ex.getMessage());
    }

    @Test
    void listBySubject_shouldThrowWhenUserDoesNotOwnSubject() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        when(subjectApi.userOwnsSubject(userId, subjectId)).thenReturn(false);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> equivalenceService.listBySubject(userId, subjectId));

        assertEquals("Materia no encontrada o sin permisos", ex.getMessage());
    }

    @Test
    void listByUser_shouldReturnMappedItems() {
        UUID userId = UUID.randomUUID();

        Equivalence equivalence = new Equivalence();
        equivalence.setId(UUID.randomUUID());
        equivalence.setUserId(userId);
        equivalence.setType("total");

        when(equivalenceRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(equivalence));

        List<EquivalenceResponseDto> result = equivalenceService.listByUser(userId);

        assertEquals(1, result.size());
        assertEquals("total", result.get(0).getType());
    }

    @Test
    void deleteEquivalence_shouldDeleteOwnedEntity() {
        UUID userId = UUID.randomUUID();
        UUID equivalenceId = UUID.randomUUID();

        Equivalence equivalence = new Equivalence();
        equivalence.setId(equivalenceId);
        equivalence.setUserId(userId);

        when(equivalenceRepository.findByIdAndUserId(equivalenceId, userId)).thenReturn(Optional.of(equivalence));

        equivalenceService.deleteEquivalence(equivalenceId, userId);

        verify(equivalenceRepository).delete(equivalence);
        assertTrue(true);
    }

    private void mockOwnedSubjects(UUID userId, UUID subjectAId, UUID subjectBId) {
        when(subjectApi.userOwnsSubject(userId, subjectAId)).thenReturn(true);
        when(subjectApi.userOwnsSubject(userId, subjectBId)).thenReturn(true);
    }

    private void mockDifferentCareers(UUID subjectAId, UUID subjectBId) {
        when(subjectApi.getSubjectBasic(subjectAId))
                .thenReturn(new SubjectBasicDto(subjectAId, UUID.randomUUID(), "Algebra", "ALG", "aprobada"));
        when(subjectApi.getSubjectBasic(subjectBId))
                .thenReturn(new SubjectBasicDto(subjectBId, UUID.randomUUID(), "Fisica", "FIS", "aprobada"));
    }

    private void mockNoDuplicatesOnCreate(UUID userId, UUID subjectAId, UUID subjectBId) {
        when(equivalenceRepository.existsByUserIdAndSubjectAIdAndSubjectBId(userId, subjectAId, subjectBId))
                .thenReturn(false);
        when(equivalenceRepository.existsByUserIdAndSubjectAIdAndSubjectBId(userId, subjectBId, subjectAId))
                .thenReturn(false);
    }
}
