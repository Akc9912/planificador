
package aktech.planificador.Model.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import aktech.planificador.Model.enums.Rol;

@Entity
@Table(name = "normal_user")
public class NormalUser extends Usuario {

    public NormalUser() {
        super();
        this.setRol(Rol.USER);
    }

    public NormalUser(String email, String passwordHash, String nombre, String apellido) {
        super(email, passwordHash, nombre, apellido, Rol.USER);
    }
}
