package aktech.planificador.Dto.materia;

import java.time.LocalTime;

import aktech.planificador.Model.enums.DiaSemana;
import aktech.planificador.Model.historial.HorarioPorMateria;

public class MateriaPlannerResponseDto {
    private String titulo;
    private String color;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private DiaSemana dia;

    // getters y setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public DiaSemana getDia() {
        return dia;
    }

    public void setDia(DiaSemana dia) {
        this.dia = dia;
    }
}
