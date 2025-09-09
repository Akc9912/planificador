package aktech.planificador.Controller;

public class RegisterResponseDTO {
    private String message;
    private String email;
    private String nombre;
    private String apellido;

    public RegisterResponseDTO(String message, String email, String nombre, String apellido) {
        this.message = message;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
