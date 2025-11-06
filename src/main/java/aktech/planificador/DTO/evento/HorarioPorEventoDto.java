package aktech.planificador.Dto.evento;

import java.time.LocalDateTime;

public class HorarioPorEventoDto {
    private LocalDateTime inicio;
    private LocalDateTime fin;

    // getters and setters
    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }
}
