package aktech.planificador.modules.subject.enums;

import java.util.Locale;

public enum SubjectWeekDay {
    LUNES,
    MARTES,
    MIERCOLES,
    JUEVES,
    VIERNES,
    SABADO,
    DOMINGO;

    public static String normalize(String rawDay) {
        if (rawDay == null || rawDay.isBlank()) {
            throw new IllegalArgumentException("El dia de la semana es obligatorio");
        }

        String normalized = rawDay.trim().toUpperCase(Locale.ROOT);
        try {
            return SubjectWeekDay.valueOf(normalized).name();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Dia de la semana invalido");
        }
    }
}
