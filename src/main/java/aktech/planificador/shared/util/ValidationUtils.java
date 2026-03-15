package aktech.planificador.shared.util;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requireNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static String requireText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo " + fieldName + " es obligatorio");
        }

        String normalized = value.trim();

        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException("El campo " + fieldName + " supera el maximo permitido");
        }

        return normalized;
    }
}
