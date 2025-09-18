package aktech.planificador.DTO.auth;

import aktech.planificador.DTO.usuarios.UserSettingsResponseDto;
import aktech.planificador.DTO.usuarios.UsuarioResponseDto;

public class LoginResponseDto {
    private String token;
    private UsuarioResponseDto usuario;
    private UserSettingsResponseDto userSettings;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioResponseDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDto usuario) {
        this.usuario = usuario;
    }

    public UserSettingsResponseDto getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettingsResponseDto userSettings) {
        this.userSettings = userSettings;
    }
}
