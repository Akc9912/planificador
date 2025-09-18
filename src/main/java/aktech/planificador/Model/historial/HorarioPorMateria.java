package aktech.planificador.Model.historial;

import jakarta.persistence.*;

import java.time.LocalTime;

import aktech.planificador.Model.core.Materia;
import aktech.planificador.Model.enums.DiaSemana;

@Entity
@Table(name = "horario_por_materia")
public class HorarioPorMateria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DiaSemana dia;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    public HorarioPorMateria() {
    }

    public HorarioPorMateria(Integer id, Materia materia, DiaSemana dia, LocalTime horaInicio,
            LocalTime horaFin) {
        this.id = id;
        this.materia = materia;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public DiaSemana getDia() {
        return dia;
    }

    public void setDia(DiaSemana dia) {
        this.dia = dia;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}
