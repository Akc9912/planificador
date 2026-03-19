package aktech.planificador.modules.career.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aktech.planificador.modules.career.dto.CareerCreateRequestDto;
import aktech.planificador.modules.career.dto.CareerResponseDto;
import aktech.planificador.modules.career.dto.CareerUpdateRequestDto;
import aktech.planificador.modules.career.enums.CareerStatus;
import aktech.planificador.modules.career.application.CareerService;
import aktech.planificador.shared.exception.BusinessException;

@RestController
@RequestMapping("/careers")
public class CareerController {

    private final CareerService careerService;

    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("No hay usuario autenticado");
        }

        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Token invalido: userId no es UUID");
        }
    }

    // Crear carrera para un usuario
    @PostMapping
    public CareerResponseDto createCareer(
            @RequestBody CareerCreateRequestDto request) {
        UUID userId = getAuthenticatedUserId();
        return careerService.createCareer(userId, request);
    }

    // Listar carreras por usuario
    @GetMapping
    public List<CareerResponseDto> listByUser() {
        UUID userId = getAuthenticatedUserId();
        return careerService.listByUser(userId);
    }

    // Listar carreras por usuario y estado
    @GetMapping("/status/{status}")
    public List<CareerResponseDto> listByUserAndStatus(
            @PathVariable CareerStatus status) {
        UUID userId = getAuthenticatedUserId();
        return careerService.listByUserAndStatus(userId, status);
    }

    // Consulta para panel de superadmin/metricas
    @GetMapping("/admin/status/{status}")
    public List<CareerResponseDto> listByStatusForAdminMetrics(@PathVariable CareerStatus status) {
        return careerService.listByStatusForAdminMetrics(status);
    }

    // Obtener una carrera validando ownership
    @GetMapping("/{id}")
    public CareerResponseDto getByIdOwned(
            @PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        return careerService.getOwnedOrThrow(id, userId);
    }

    // Validar ownership de carrera
    @GetMapping("/{id}/ownership")
    public boolean ownsCareer(
            @PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        return careerService.ownsCareer(id, userId);
    }

    // Actualizar carrera
    @PutMapping("/{id}")
    public CareerResponseDto updateCareer(
            @PathVariable UUID id,
            @RequestBody CareerUpdateRequestDto request) {
        UUID userId = getAuthenticatedUserId();
        return careerService.updateCareer(id, userId, request);
    }

    // Eliminar carrera
    @DeleteMapping("/{id}")
    public void deleteCareer(@PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        careerService.deleteCareer(id, userId);
    }
}
