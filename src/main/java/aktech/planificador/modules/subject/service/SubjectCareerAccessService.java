package aktech.planificador.modules.subject.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import aktech.planificador.Exception.ResourceNotFoundException;
import aktech.planificador.shared.api.CareerApi;

@Service
public class SubjectCareerAccessService {

    private final CareerApi careerApi;

    public SubjectCareerAccessService(CareerApi careerApi) {
        this.careerApi = careerApi;
    }

    public void validateCareerOwnership(UUID userId, UUID careerId) {
        if (!careerApi.existsCareer(careerId)) {
            throw new ResourceNotFoundException("Carrera no encontrada");
        }

        if (!careerApi.userOwnsCareer(userId, careerId)) {
            throw new ResourceNotFoundException("La carrera no pertenece al usuario");
        }
    }
}
