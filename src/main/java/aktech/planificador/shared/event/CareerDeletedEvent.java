package aktech.planificador.shared.event;

import java.util.UUID;

public class CareerDeletedEvent {
    private final UUID careerId;
    private final UUID userId;

    public CareerDeletedEvent(UUID careerId, UUID userId) {
        this.careerId = careerId;
        this.userId = userId;
    }

    public UUID getCareerId() {
        return careerId;
    }

    public UUID getUserId() {
        return userId;
    }
}
