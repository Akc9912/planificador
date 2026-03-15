package aktech.planificador.modules.career.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import aktech.planificador.modules.career.dto.CareerCreateRequestDto;
import aktech.planificador.modules.career.dto.CareerResponseDto;
import aktech.planificador.modules.career.dto.CareerUpdateRequestDto;
import aktech.planificador.modules.career.enums.CareerStatus;
import aktech.planificador.modules.career.model.Career;
import aktech.planificador.modules.career.repository.CareerRepository;
import aktech.planificador.shared.api.CareerApi;
import aktech.planificador.shared.dto.CareerBasicDto;
import aktech.planificador.shared.event.CareerDeletedEvent;
import aktech.planificador.shared.exception.NotFoundException;
import aktech.planificador.shared.util.ValidationUtils;

@Service
@Transactional(readOnly = true)
public class CareerService implements CareerApi {
    private static final int MAX_TEXT_LENGTH = 150;

    private final CareerRepository careerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CareerService(CareerRepository careerRepository, ApplicationEventPublisher eventPublisher) {
        this.careerRepository = careerRepository;
        this.eventPublisher = eventPublisher;
    }

    // ----------- Metodos de lectura -----------

    // Consulta por usuario
    public List<CareerResponseDto> listByUser(UUID userId) {
        requireUserId(userId);
        return toResponseDtoList(careerRepository.findByUserId(userId));
    }

    // Consulta por usuario y estado
    public List<CareerResponseDto> listByUserAndStatus(UUID userId, CareerStatus status) {
        requireUserId(userId);
        requireStatus(status);
        return toResponseDtoList(careerRepository.findByUserIdAndStatus(userId, status));
    }

    // Consulta global para metricas de superadmin.
    // Proteger este metodo en la capa de controlador/seguridad.
    public List<CareerResponseDto> listByStatusForAdminMetrics(CareerStatus status) {
        requireStatus(status);
        return toResponseDtoList(careerRepository.findByStatus(status));
    }

    // Validacion de ownership cargando el registro en una sola consulta
    public CareerResponseDto getOwnedOrThrow(UUID id, UUID userId) {
        return toResponseDto(getOwnedEntityOrThrow(id, userId));
    }

    private Career getOwnedEntityOrThrow(UUID id, UUID userId) {
        requireCareerId(id);
        requireUserId(userId);
        return careerRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Carrera no encontrada o sin permisos"));
    }

    public boolean ownsCareer(UUID id, UUID userId) {
        requireCareerId(id);
        requireUserId(userId);
        return careerRepository.existsByIdAndUserId(id, userId);
    }

    @Override
    public CareerBasicDto getCareerBasic(UUID careerId) {
        requireCareerId(careerId);

        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new NotFoundException("Carrera no encontrada"));

        return toCareerBasicDto(career);
    }

    @Override
    public boolean existsCareer(UUID careerId) {
        requireCareerId(careerId);
        return careerRepository.existsById(careerId);
    }

    @Override
    public boolean userOwnsCareer(UUID userId, UUID careerId) {
        requireUserId(userId);
        requireCareerId(careerId);
        return careerRepository.existsByIdAndUserId(careerId, userId);
    }

    // ----------- Metodos de escritura -----------

    @Transactional
    public CareerResponseDto createCareer(UUID userId, CareerCreateRequestDto request) {
        requireUserId(userId);
        requireCreateRequest(request);

        Career career = new Career();
        career.setUserId(userId);
        career.setName(validateAndNormalizeText(request.getName(), "nombre"));
        career.setInstitution(validateAndNormalizeText(request.getInstitution(), "institucion"));
        career.setStatus(CareerStatus.NOT_STARTED);
        career.setStartDate(request.getStartDate());
        career.setHasHours(Boolean.TRUE.equals(request.getHasHours()));
        career.setHasCredits(Boolean.TRUE.equals(request.getHasCredits()));

        return toResponseDto(careerRepository.save(career));
    }

    @Transactional
    public CareerResponseDto updateCareer(UUID id, UUID userId, CareerUpdateRequestDto request) {
        requireUpdateRequest(request);
        Career career = getOwnedEntityOrThrow(id, userId);

        if (request.getName() != null) {
            career.setName(validateAndNormalizeText(request.getName(), "nombre"));
        }

        if (request.getInstitution() != null) {
            career.setInstitution(validateAndNormalizeText(request.getInstitution(), "institucion"));
        }

        if (request.getStartDate() != null) {
            career.setStartDate(request.getStartDate());
        }

        if (request.getStatus() != null) {
            career.setStatus(request.getStatus());
        }

        if (request.getHasHours() != null) {
            career.setHasHours(request.getHasHours());
        }

        if (request.getHasCredits() != null) {
            career.setHasCredits(request.getHasCredits());
        }

        return toResponseDto(careerRepository.save(career));
    }

    @Transactional
    public void deleteCareer(UUID id, UUID userId) {
        Career career = getOwnedEntityOrThrow(id, userId);
        careerRepository.delete(career);
        eventPublisher.publishEvent(new CareerDeletedEvent(id, userId));
    }

    // ----------- Validaciones internas -----------

    private void requireCreateRequest(CareerCreateRequestDto request) {
        ValidationUtils.requireNotNull(request, "El body de creacion es obligatorio");
    }

    private void requireUpdateRequest(CareerUpdateRequestDto request) {
        ValidationUtils.requireNotNull(request, "El body de actualizacion es obligatorio");
    }

    private void requireCareerId(UUID id) {
        ValidationUtils.requireNotNull(id, "El id de carrera es obligatorio");
    }

    private void requireUserId(UUID userId) {
        ValidationUtils.requireNotNull(userId, "El id de usuario es obligatorio");
    }

    private void requireStatus(CareerStatus status) {
        ValidationUtils.requireNotNull(status, "El estado es obligatorio");
    }

    private String validateAndNormalizeText(String value, String fieldName) {
        return ValidationUtils.requireText(value, fieldName, MAX_TEXT_LENGTH);
    }

    private List<CareerResponseDto> toResponseDtoList(List<Career> careers) {
        return careers.stream().map(this::toResponseDto).toList();
    }

    private CareerResponseDto toResponseDto(Career career) {
        CareerResponseDto dto = new CareerResponseDto();
        dto.setId(career.getId());
        dto.setUserId(career.getUserId());
        dto.setName(career.getName());
        dto.setInstitution(career.getInstitution());
        dto.setStatus(career.getStatus());
        dto.setStartDate(career.getStartDate());
        dto.setHasHours(career.isHasHours());
        dto.setHasCredits(career.isHasCredits());
        dto.setCreatedAt(career.getCreatedAt());
        dto.setUpdatedAt(career.getUpdatedAt());
        return dto;
    }

    private CareerBasicDto toCareerBasicDto(Career career) {
        return new CareerBasicDto(
                career.getId(),
                career.getUserId(),
                career.getName(),
                career.getInstitution());
    }
}
