package aktech.planificador.DTO.auth;

import aktech.planificador.DTO.usuarios.UserSettingsResponseDTO;
import aktech.planificador.DTO.usuarios.UsuarioResponseDTO;

public class LoginResponseDTO {
    private String token;
    private UsuarioResponseDTO usuario;
    private UserSettingsResponseDTO userSettings;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDTO usuario) {
        this.usuario = usuario;
    }

    public UserSettingsResponseDTO getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettingsResponseDTO userSettings) {
        this.userSettings = userSettings;
    }
}
