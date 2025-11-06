package aktech.planificador.Model.soporte;

import aktech.planificador.Model.enums.SoporteTipo;

public class Soporte {
    private int id;
    private int idUsuario;
    private SoporteTipo tipo;
    private String titulo;
    private String mensaje;

    public Soporte(int idUsuario, SoporteTipo tipo, String titulo, String mensaje) {
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
    }
}
