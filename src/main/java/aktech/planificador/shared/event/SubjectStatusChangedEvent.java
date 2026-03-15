package aktech.planificador.shared.event;

import java.util.UUID;

public class SubjectStatusChangedEvent {
    private final UUID subjectId;
    private final UUID careerId;
    private final String oldStatus;
    private final String newStatus;

    public SubjectStatusChangedEvent(UUID subjectId, UUID careerId, String oldStatus, String newStatus) {
        this.subjectId = subjectId;
        this.careerId = careerId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public UUID getSubjectId() {
        return subjectId;
    }

    public UUID getCareerId() {
        return careerId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }
}
