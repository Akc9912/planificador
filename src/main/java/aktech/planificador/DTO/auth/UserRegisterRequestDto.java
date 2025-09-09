package aktech.planificador.DTO.auth;

public class UserRegisterRequestDto {
    private String email;
    private String password;
    private String nombre;
    private String apellido;

    public UserRegisterRequestDto(String email, String password, String nombre, String apellido) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }
}
