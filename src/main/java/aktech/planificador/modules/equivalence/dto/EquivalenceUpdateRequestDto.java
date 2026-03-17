package aktech.planificador.modules.equivalence.dto;

import java.util.UUID;

public class EquivalenceUpdateRequestDto {
    private String type;
    private UUID subjectAId;
    private UUID subjectBId;
    private String notes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getSubjectAId() {
        return subjectAId;
    }

    public void setSubjectAId(UUID subjectAId) {
        this.subjectAId = subjectAId;
    }

    public UUID getSubjectBId() {
        return subjectBId;
    }

    public void setSubjectBId(UUID subjectBId) {
        this.subjectBId = subjectBId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
