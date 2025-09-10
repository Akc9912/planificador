package aktech.planificador.Model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "horario_por_materia")
@IdClass(HorarioPorMateria.PK.class)
public class HorarioPorMateria {
    @Id
    @Column(name = "id_materia")
    private int idMateria;

    @Id
    @Column(name = "id_horario")
    private int idHorario;

    // Getters y setters
    public int getIdMateria() { return idMateria; }
    public void setIdMateria(int idMateria) { this.idMateria = idMateria; }
    public int getIdHorario() { return idHorario; }
    public void setIdHorario(int idHorario) { this.idHorario = idHorario; }

    // PK compuesta
    public static class PK implements Serializable {
        private int idMateria;
        private int idHorario;
        public PK() {}
        public PK(int idMateria, int idHorario) {
            this.idMateria = idMateria;
            this.idHorario = idHorario;
        }
        // equals y hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PK pk = (PK) o;
            return idMateria == pk.idMateria && idHorario == pk.idHorario;
        }
        @Override
        public int hashCode() {
            return idMateria + idHorario;
        }
    }
}
