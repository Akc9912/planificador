package com.micarrera.modules.subject.api;

import java.util.UUID;

import com.micarrera.shared.dto.SubjectBasicDto;

public interface SubjectApi {

    SubjectBasicDto getSubjectBasic(UUID subjectId);

    boolean existsSubject(UUID subjectId);

    boolean userOwnsSubject(UUID userId, UUID subjectId);
}
