package aktech.planificador.modules.career.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import aktech.planificador.modules.career.dto.CareerCreateRequestDto;
import aktech.planificador.modules.career.dto.CareerResponseDto;
import aktech.planificador.modules.career.dto.CareerUpdateRequestDto;
import aktech.planificador.modules.career.enums.CareerStatus;
import aktech.planificador.modules.career.model.Career;
import aktech.planificador.modules.career.repository.CareerRepository;
import aktech.planificador.shared.dto.CareerBasicDto;
import aktech.planificador.shared.event.CareerDeletedEvent;
import aktech.planificador.shared.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class CareerServiceTest {

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private CareerService careerService;

    @BeforeEach
    void setUp() {
        careerService = new CareerService(careerRepository, eventPublisher);
    }

    @Test
    void createCareer_shouldCreateWithNormalizedFieldsAndDefaults() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();

        CareerCreateRequestDto request = new CareerCreateRequestDto();
        request.setName("  Ingenieria en Sistemas  ");
        request.setInstitution("  UTN  ");
        request.setStartDate(LocalDate.of(2026, 3, 1));
        request.setHasHours(null);
        request.setHasCredits(true);

        when(careerRepository.save(any(Career.class))).thenAnswer(invocation -> {
            Career saved = invocation.getArgument(0);
            saved.setId(careerId);
            return saved;
        });

        CareerResponseDto response = careerService.createCareer(userId, request);

        ArgumentCaptor<Career> captor = ArgumentCaptor.forClass(Career.class);
        verify(careerRepository).save(captor.capture());

        Career savedCareer = captor.getValue();
        assertEquals(userId, savedCareer.getUserId());
        assertEquals("Ingenieria en Sistemas", savedCareer.getName());
        assertEquals("UTN", savedCareer.getInstitution());
        assertEquals(CareerStatus.NOT_STARTED, savedCareer.getStatus());
        assertFalse(savedCareer.isHasHours());
        assertTrue(savedCareer.isHasCredits());

        assertEquals(careerId, response.getId());
        assertEquals("Ingenieria en Sistemas", response.getName());
    }

    @Test
    void createCareer_shouldThrowWhenRequestIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> careerService.createCareer(UUID.randomUUID(), null));

        assertEquals("El body de creacion es obligatorio", ex.getMessage());
    }

    @Test
    void listByUser_shouldMapRepositoryResults() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();

        Career career = createCareer(careerId, userId, "Sistemas", "UTN");
        when(careerRepository.findByUserId(userId)).thenReturn(List.of(career));

        List<CareerResponseDto> result = careerService.listByUser(userId);

        assertEquals(1, result.size());
        assertEquals(careerId, result.get(0).getId());
        assertEquals("Sistemas", result.get(0).getName());
    }

    @Test
    void getOwnedOrThrow_shouldThrowWhenCareerNotFound() {
        UUID careerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(careerRepository.findByIdAndUserId(careerId, userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> careerService.getOwnedOrThrow(careerId, userId));

        assertEquals("Carrera no encontrada o sin permisos", ex.getMessage());
    }

    @Test
    void updateCareer_shouldUpdateOnlyProvidedFields() {
        UUID careerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Career existing = createCareer(careerId, userId, "Viejo Nombre", "Vieja Institucion");
        existing.setStatus(CareerStatus.NOT_STARTED);
        existing.setHasHours(false);
        existing.setHasCredits(false);

        CareerUpdateRequestDto request = new CareerUpdateRequestDto();
        request.setName("  Nuevo Nombre  ");
        request.setStatus(CareerStatus.IN_PROGRESS);
        request.setHasCredits(true);

        when(careerRepository.findByIdAndUserId(careerId, userId)).thenReturn(Optional.of(existing));
        when(careerRepository.save(any(Career.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CareerResponseDto response = careerService.updateCareer(careerId, userId, request);

        assertEquals("Nuevo Nombre", response.getName());
        assertEquals("Vieja Institucion", response.getInstitution());
        assertEquals(CareerStatus.IN_PROGRESS, response.getStatus());
        assertFalse(response.isHasHours());
        assertTrue(response.isHasCredits());
    }

    @Test
    void deleteCareer_shouldDeleteAndPublishEvent() {
        UUID careerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Career existing = createCareer(careerId, userId, "Sistemas", "UTN");
        when(careerRepository.findByIdAndUserId(careerId, userId)).thenReturn(Optional.of(existing));

        careerService.deleteCareer(careerId, userId);

        verify(careerRepository).delete(existing);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        Object event = eventCaptor.getValue();
        assertInstanceOf(CareerDeletedEvent.class, event);
        CareerDeletedEvent deletedEvent = (CareerDeletedEvent) event;
        assertEquals(careerId, deletedEvent.getCareerId());
        assertEquals(userId, deletedEvent.getUserId());
    }

    @Test
    void getCareerBasic_shouldReturnMappedDto() {
        UUID careerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Career career = createCareer(careerId, userId, "Sistemas", "UTN");
        when(careerRepository.findById(careerId)).thenReturn(Optional.of(career));

        CareerBasicDto result = careerService.getCareerBasic(careerId);

        assertEquals(careerId, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals("Sistemas", result.getName());
        assertEquals("UTN", result.getInstitution());
    }

    @Test
    void userOwnsCareer_shouldUseRepositoryWithOwnershipOrder() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();

        when(careerRepository.existsByIdAndUserId(careerId, userId)).thenReturn(true);

        boolean result = careerService.userOwnsCareer(userId, careerId);

        assertTrue(result);
        verify(careerRepository).existsByIdAndUserId(careerId, userId);
    }

    private Career createCareer(UUID id, UUID userId, String name, String institution) {
        Career career = new Career();
        career.setId(id);
        career.setUserId(userId);
        career.setName(name);
        career.setInstitution(institution);
        return career;
    }
}
