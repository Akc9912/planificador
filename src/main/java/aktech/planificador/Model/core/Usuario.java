package aktech.planificador.Model.core;

import jakarta.persistence.*;

import aktech.planificador.Model.enums.Rol;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String pass;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;
    @Column(name = "activo", nullable = false)
    private boolean isActive;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
    @Column(name = "cambiar_pass", nullable = false)
    private boolean cambiarPass;

    public Usuario(String email, String nombre, String apellido, Rol rol) {
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.isActive = true;
        this.cambiarPass = true;
        this.rol = rol;
    }

    // constuctor con password para registrar usuario
    public Usuario(String email, String password, String nombre, String apellido, Rol rol) {
        this.email = email;
        this.pass = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.isActive = true;
        this.cambiarPass = false;
        this.rol = rol;
    }

    public Usuario() {
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return pass;
    }

    public void setPassword(String password) {
        this.pass = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
    public boolean isCambiarPass() {
        return cambiarPass;
    }
    public void setCambiarPass(boolean cambiarPass) {
        this.cambiarPass = cambiarPass;
    }
}
