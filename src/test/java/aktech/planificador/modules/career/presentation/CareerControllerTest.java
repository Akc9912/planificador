package aktech.planificador.modules.career.presentation;

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

import aktech.planificador.modules.career.dto.CareerCreateRequestDto;
import aktech.planificador.modules.career.dto.CareerResponseDto;
import aktech.planificador.modules.career.dto.CareerUpdateRequestDto;
import aktech.planificador.modules.career.enums.CareerStatus;
import aktech.planificador.modules.career.application.CareerService;
import aktech.planificador.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class CareerControllerTest {

    @Mock
    private CareerService careerService;

    private CareerController careerController;

    @BeforeEach
    void setUp() {
        careerController = new CareerController(careerService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createCareer_shouldDelegateUsingAuthenticatedUserId() {
        UUID userId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        CareerCreateRequestDto request = new CareerCreateRequestDto();
        CareerResponseDto expected = new CareerResponseDto();

        when(careerService.createCareer(userId, request)).thenReturn(expected);

        CareerResponseDto response = careerController.createCareer(request);

        assertSame(expected, response);
        verify(careerService).createCareer(userId, request);
    }

    @Test
    void listByUser_shouldDelegateUsingAuthenticatedUserId() {
        UUID userId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<CareerResponseDto> expected = List.of(new CareerResponseDto());
        when(careerService.listByUser(userId)).thenReturn(expected);

        List<CareerResponseDto> response = careerController.listByUser();

        assertSame(expected, response);
        verify(careerService).listByUser(userId);
    }

    @Test
    void listByUserAndStatus_shouldDelegateUsingAuthenticatedUserId() {
        UUID userId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<CareerResponseDto> expected = List.of(new CareerResponseDto());
        when(careerService.listByUserAndStatus(userId, CareerStatus.IN_PROGRESS)).thenReturn(expected);

        List<CareerResponseDto> response = careerController.listByUserAndStatus(CareerStatus.IN_PROGRESS);

        assertSame(expected, response);
        verify(careerService).listByUserAndStatus(userId, CareerStatus.IN_PROGRESS);
    }

    @Test
    void getByIdOwned_shouldDelegateUsingAuthenticatedUserId() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        CareerResponseDto expected = new CareerResponseDto();
        when(careerService.getOwnedOrThrow(careerId, userId)).thenReturn(expected);

        CareerResponseDto response = careerController.getByIdOwned(careerId);

        assertSame(expected, response);
        verify(careerService).getOwnedOrThrow(careerId, userId);
    }

    @Test
    void updateCareer_shouldDelegateUsingAuthenticatedUserId() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        CareerUpdateRequestDto request = new CareerUpdateRequestDto();
        CareerResponseDto expected = new CareerResponseDto();

        when(careerService.updateCareer(careerId, userId, request)).thenReturn(expected);

        CareerResponseDto response = careerController.updateCareer(careerId, request);

        assertSame(expected, response);
        verify(careerService).updateCareer(careerId, userId, request);
    }

    @Test
    void deleteCareer_shouldDelegateUsingAuthenticatedUserId() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        careerController.deleteCareer(careerId);

        verify(careerService).deleteCareer(careerId, userId);
    }

    @Test
    void ownsCareer_shouldDelegateUsingAuthenticatedUserId() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        when(careerService.ownsCareer(careerId, userId)).thenReturn(true);

        boolean response = careerController.ownsCareer(careerId);

        assertEquals(true, response);
        verify(careerService).ownsCareer(careerId, userId);
    }

    @Test
    void listByStatusForAdminMetrics_shouldNotRequireAuthenticatedUser() {
        List<CareerResponseDto> expected = List.of(new CareerResponseDto());
        when(careerService.listByStatusForAdminMetrics(CareerStatus.IN_PROGRESS)).thenReturn(expected);

        List<CareerResponseDto> response = careerController.listByStatusForAdminMetrics(CareerStatus.IN_PROGRESS);

        assertSame(expected, response);
        verify(careerService).listByStatusForAdminMetrics(CareerStatus.IN_PROGRESS);
    }

    @Test
    void listByUser_shouldThrowWhenNoAuthenticatedUser() {
        SecurityContextHolder.clearContext();

        BusinessException ex = assertThrows(BusinessException.class, () -> careerController.listByUser());

        assertEquals("No hay usuario autenticado", ex.getMessage());
    }

    @Test
    void getByIdOwned_shouldThrowWhenNoAuthenticatedUser() {
        SecurityContextHolder.clearContext();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> careerController.getByIdOwned(UUID.randomUUID()));

        assertEquals("No hay usuario autenticado", ex.getMessage());
    }

    @Test
    void ownsCareer_shouldThrowWhenTokenUserIdIsNotUuid() {
        setAuthenticatedUser("not-a-uuid");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> careerController.ownsCareer(UUID.randomUUID()));

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
