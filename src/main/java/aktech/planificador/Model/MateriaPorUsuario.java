package aktech.planificador.Model;

import aktech.planificador.Model.core.Usuario;

public class MateriaPorUsuario {
    private Usuario usuario;
    private Materia materia;

    public MateriaPorUsuario(Usuario usuario, Materia materia) {
        this.usuario = usuario;
        this.materia = materia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

}
