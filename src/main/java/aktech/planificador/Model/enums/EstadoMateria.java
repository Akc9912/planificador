package aktech.planificador.Model.enums;

public enum EstadoMateria {
    CURSANDO("Cursando"),
    SIN_CURSAR("Sin Cursar"),
    DEBO_FINAL("Debo Final"),
    APROBADA("Aprobada");

    private String estado;

    EstadoMateria(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }
}
