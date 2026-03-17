package aktech.planificador.modules.equivalence.enums;

import java.util.Locale;
import java.util.Set;

public enum EquivalenceType {
    TOTAL("total"),
    PARCIAL("parcial");

    private static final Set<String> VALID_VALUES = Set.of("total", "parcial");

    private final String value;

    EquivalenceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String normalizeRequired(String rawType) {
        if (rawType == null || rawType.isBlank()) {
            throw new IllegalArgumentException("El tipo de equivalencia es obligatorio");
        }

        String normalized = rawType.trim().toLowerCase(Locale.ROOT);
        if (!VALID_VALUES.contains(normalized)) {
            throw new IllegalArgumentException("Tipo de equivalencia invalido");
        }

        return normalized;
    }
}
