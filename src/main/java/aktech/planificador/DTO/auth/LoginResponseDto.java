package aktech.planificador.Dto.auth;

import aktech.planificador.Dto.usuarios.UserSettingsResponseDto;
import aktech.planificador.Dto.usuarios.UsuarioResponseDto;

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
