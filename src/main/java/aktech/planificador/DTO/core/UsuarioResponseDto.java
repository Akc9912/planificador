package aktech.planificador.DTO.core;

public class UsuarioResponseDto {
    private int id;
    private String email;
    private String nombre;
    private String apellido;
    private boolean isActive;
    private boolean cambiarPass;
    private String role;

    public UsuarioResponseDto() {
    }

    public UsuarioResponseDto(int id, String email, String nombre, String apellido, boolean isActive, boolean cambiarPass, String role) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.isActive = isActive;
        this.cambiarPass = cambiarPass;
        this.role = role;
    }

    // Getters y setters
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

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isCambiarPass() {
        return cambiarPass;
    }

    public void setCambiarPass(boolean cambiarPass) {
        this.cambiarPass = cambiarPass;
    }
}
