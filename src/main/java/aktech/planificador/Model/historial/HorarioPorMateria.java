package aktech.planificador.Model.historial;

import jakarta.persistence.*;

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

    private java.time.LocalTime horaInicio;
    private java.time.LocalTime horaFin;

    public HorarioPorMateria() {
    }

    public HorarioPorMateria(Integer id, Materia materia, DiaSemana dia, java.time.LocalTime horaInicio,
            java.time.LocalTime horaFin) {
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

    public java.time.LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(java.time.LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public java.time.LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(java.time.LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}
