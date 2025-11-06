package aktech.planificador.Model.historial;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import aktech.planificador.Model.core.Evento;

@Entity
@Table(name = "horario_por_evento")
public class HorarioPorEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    private LocalDateTime inicio;
    private LocalDateTime fin;

    public HorarioPorEvento(Evento evento, LocalDateTime inicio, LocalDateTime fin) {
        this.evento = evento;
        this.inicio = inicio;
        this.fin = fin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

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
