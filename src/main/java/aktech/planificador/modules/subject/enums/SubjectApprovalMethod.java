package aktech.planificador.modules.subject.enums;

import java.util.Locale;
import java.util.Set;

public enum SubjectApprovalMethod {
    PROMOCION("promocion"),
    EXAMEN_FINAL("examen_final"),
    EXAMEN_LIBRE("examen_libre");

    private static final Set<String> VALID_VALUES = Set.of(
            "promocion",
            "examen_final",
            "examen_libre");

    private final String value;

    SubjectApprovalMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String normalize(String rawMethod) {
        if (rawMethod == null) {
            return null;
        }

        if (rawMethod.isBlank()) {
            throw new IllegalArgumentException("Metodo de aprobacion invalido");
        }

        String normalized = rawMethod.trim().toLowerCase(Locale.ROOT);
        if (!VALID_VALUES.contains(normalized)) {
            throw new IllegalArgumentException("Metodo de aprobacion invalido");
        }

        return normalized;
    }
}
