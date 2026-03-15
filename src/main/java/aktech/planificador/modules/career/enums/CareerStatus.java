package aktech.planificador.modules.career.enums;

public enum CareerStatus {
    NOT_STARTED("career.status.not_started"),
    IN_PROGRESS("career.status.in_progress"),
    PAUSED("career.status.paused"),
    COMPLETED("career.status.completed");

    private final String labelKey;

    CareerStatus(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public boolean isFinal() {
        return this == COMPLETED;
    }

    public boolean isActive() {
        return this == IN_PROGRESS;
    }
}
