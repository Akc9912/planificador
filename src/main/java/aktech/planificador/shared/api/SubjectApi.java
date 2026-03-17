package aktech.planificador.shared.api;

import java.util.UUID;

import aktech.planificador.shared.dto.SubjectBasicDto;

public interface SubjectApi {

    SubjectBasicDto getSubjectBasic(UUID subjectId);

    boolean existsSubject(UUID subjectId);

    boolean userOwnsSubject(UUID userId, UUID subjectId);
}
