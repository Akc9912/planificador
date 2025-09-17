
package aktech.planificador.Model.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import aktech.planificador.Model.enums.Rol;

@Entity
@Table(name = "admin")
public class Admin extends Usuario {
    public Admin() {
        super();
        this.setRol(Rol.ADMIN);
    }

    public Admin(String email, String passwordHash, String nombre, String apellido) {
        super(email, passwordHash, nombre, apellido, Rol.ADMIN);
    }
}
