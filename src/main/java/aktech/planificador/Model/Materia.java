package aktech.planificador.Model;

import aktech.planificador.Model.enums.EstadoMateria;

public class Materia {
    private int id;
    private String titulo;
    private EstadoMateria estado;
    private boolean active;
    private String color;

    public Materia() {
        this.active = true;
    }

    public Materia(String titulo, EstadoMateria estado, String color) {
        this.titulo = titulo;
        this.estado = estado;
        this.active = true;
        this.color = color;
    }


    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public EstadoMateria getEstado() {
        return estado;
    }

    public boolean isActive() {
        return active;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setEstado(EstadoMateria estado) {
        this.estado = estado;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
