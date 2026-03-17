package aktech.planificador.modules.subject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import aktech.planificador.modules.subject.dto.SubjectModuleCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectModuleResponseDto;
import aktech.planificador.modules.subject.dto.SubjectModuleUpdateRequestDto;
import aktech.planificador.modules.subject.model.Subject;
import aktech.planificador.modules.subject.model.SubjectModule;
import aktech.planificador.modules.subject.repository.SubjectModuleRepository;
import aktech.planificador.shared.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class SubjectModuleServiceTest {

    @Mock
    private SubjectModuleRepository subjectModuleRepository;

    @Mock
    private SubjectCareerAccessService subjectCareerAccessService;

    private SubjectModuleService subjectModuleService;

    @BeforeEach
    void setUp() {
        subjectModuleService = new SubjectModuleService(subjectModuleRepository, subjectCareerAccessService);
    }

    @Test
    void createModule_shouldCreateForOwnedSubject() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId))
                .thenReturn(createOwnedSubject(subjectId));

        SubjectModuleCreateRequestDto request = new SubjectModuleCreateRequestDto();
        request.setName("  Parcial 1 ");
        request.setGrade(new BigDecimal("8.50"));
        request.setModuleOrder(2);

        when(subjectModuleRepository.save(any(SubjectModule.class))).thenAnswer(invocation -> {
            SubjectModule module = invocation.getArgument(0);
            module.setId(moduleId);
            return module;
        });

        SubjectModuleResponseDto response = subjectModuleService.createModule(userId, subjectId, request);

        verify(subjectCareerAccessService).getOwnedSubjectOrThrow(userId, subjectId);

        ArgumentCaptor<SubjectModule> captor = ArgumentCaptor.forClass(SubjectModule.class);
        verify(subjectModuleRepository).save(captor.capture());

        SubjectModule saved = captor.getValue();
        assertEquals(subjectId, saved.getSubjectId());
        assertEquals("Parcial 1", saved.getName());
        assertEquals(new BigDecimal("8.50"), saved.getGrade());
        assertEquals(2, saved.getModuleOrder());

        assertEquals(moduleId, response.getId());
        assertEquals("Parcial 1", response.getName());
    }

    @Test
    void listBySubject_shouldReturnMappedModules() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId))
                .thenReturn(createOwnedSubject(subjectId));

        SubjectModule module = new SubjectModule();
        module.setId(UUID.randomUUID());
        module.setSubjectId(subjectId);
        module.setName("Parcial");
        module.setModuleOrder(1);

        when(subjectModuleRepository.findBySubjectIdOrderByModuleOrderAscCreatedAtAsc(subjectId))
                .thenReturn(List.of(module));

        List<SubjectModuleResponseDto> response = subjectModuleService.listBySubject(userId, subjectId);

        assertEquals(1, response.size());
        assertEquals("Parcial", response.get(0).getName());
    }

    @Test
    void updateModule_shouldThrowWhenModuleDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId))
                .thenReturn(createOwnedSubject(subjectId));
        when(subjectModuleRepository.findByIdAndSubjectId(moduleId, subjectId)).thenReturn(Optional.empty());

        SubjectModuleUpdateRequestDto request = new SubjectModuleUpdateRequestDto();
        request.setName("Nuevo nombre");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> subjectModuleService.updateModule(userId, subjectId, moduleId, request));

        assertEquals("Modulo evaluable no encontrado", ex.getMessage());
    }

    @Test
    void deleteModule_shouldDeleteWhenExists() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId))
                .thenReturn(createOwnedSubject(subjectId));

        SubjectModule module = new SubjectModule();
        module.setId(moduleId);
        module.setSubjectId(subjectId);

        when(subjectModuleRepository.findByIdAndSubjectId(moduleId, subjectId)).thenReturn(Optional.of(module));

        subjectModuleService.deleteModule(userId, subjectId, moduleId);

        verify(subjectModuleRepository).delete(module);
    }

    private Subject createOwnedSubject(UUID subjectId) {
        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setCareerId(UUID.randomUUID());
        return subject;
    }
}
