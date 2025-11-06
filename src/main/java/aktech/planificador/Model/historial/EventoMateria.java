package aktech.planificador.Model.historial;

import jakarta.persistence.*;

import java.util.Optional;

import aktech.planificador.Model.core.Evento;
import aktech.planificador.Model.core.Materia;

@Entity
@Table(name = "evento_materia")
public class EventoMateria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento")
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia")
    private Optional<Materia> materia;

    public EventoMateria() {
    }

    public EventoMateria(Evento evento, Optional<Materia> materia2) {
        this.evento = evento;
        this.materia = materia2;
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

    public Optional<Materia> getMateria() {
        return materia;
    }

    public void setMateria(Optional<Materia> materia) {
        this.materia = materia;
    }
}
