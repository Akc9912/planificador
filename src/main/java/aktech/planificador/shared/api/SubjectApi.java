package aktech.planificador.shared.api;

import java.util.UUID;

public interface SubjectApi {

    boolean existsSubject(UUID subjectId);

    boolean userOwnsSubject(UUID userId, UUID subjectId);
}
