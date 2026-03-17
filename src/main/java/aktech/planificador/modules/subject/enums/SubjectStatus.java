package aktech.planificador.modules.subject.enums;

import java.util.Locale;
import java.util.Set;

public enum SubjectStatus {
    PENDIENTE("pendiente"),
    CURSANDO("cursando"),
    REGULAR("regular"),
    APROBADA("aprobada"),
    LIBRE("libre");

    private static final Set<String> VALID_VALUES = Set.of(
            "pendiente",
            "cursando",
            "regular",
            "aprobada",
            "libre");

    private final String value;

    SubjectStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String normalize(String rawStatus) {
        if (rawStatus == null) {
            return null;
        }

        if (rawStatus.isBlank()) {
            throw new IllegalArgumentException("Estado de materia invalido");
        }

        String normalized = rawStatus.trim().toLowerCase(Locale.ROOT);
        if (!VALID_VALUES.contains(normalized)) {
            throw new IllegalArgumentException("Estado de materia invalido");
        }

        return normalized;
    }
}
