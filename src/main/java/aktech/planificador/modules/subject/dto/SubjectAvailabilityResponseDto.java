package aktech.planificador.modules.subject.dto;

import java.util.List;
import java.util.UUID;

public class SubjectAvailabilityResponseDto {
    private UUID subjectId;
    private boolean blocked;
    private boolean available;
    private List<UUID> correlatives;
    private List<UUID> missingCorrelatives;

    public UUID getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(UUID subjectId) {
        this.subjectId = subjectId;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<UUID> getCorrelatives() {
        return correlatives;
    }

    public void setCorrelatives(List<UUID> correlatives) {
        this.correlatives = correlatives;
    }

    public List<UUID> getMissingCorrelatives() {
        return missingCorrelatives;
    }

    public void setMissingCorrelatives(List<UUID> missingCorrelatives) {
        this.missingCorrelatives = missingCorrelatives;
    }
}
