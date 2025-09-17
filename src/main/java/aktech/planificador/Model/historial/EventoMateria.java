package aktech.planificador.Model.historial;

import jakarta.persistence.*;

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
    private Materia materia;

    public EventoMateria() {
    }

    public EventoMateria(Evento evento, Materia materia) {
        this.evento = evento;
        this.materia = materia;
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

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }
}
