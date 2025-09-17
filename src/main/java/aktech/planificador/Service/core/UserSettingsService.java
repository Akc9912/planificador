package aktech.planificador.Service.core;

import org.springframework.stereotype.Service;
import aktech.planificador.DTO.usuarios.UserSettingsResponseDTO;
import aktech.planificador.Model.enums.Theme;
import aktech.planificador.Repository.core.UserSettingsRepository;
import aktech.planificador.Model.core.UserSettings;
import aktech.planificador.Model.core.Usuario;
import aktech.planificador.Model.enums.DiaSemana;
import java.util.Optional;
import java.util.List;

@Service
public class UserSettingsService {
    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    public UserSettings setDefaultSettings(Usuario usuario) {
        UserSettings settings = new UserSettings();
        settings.setTheme(Theme.LIGHT);
        settings.setNotificaciones(true);
        settings.setFormatoHora(true);
        settings.setInicioPlanner(8);
        settings.setFinPlanner(20);
        settings.setPrimerDia(DiaSemana.LUNES);
        settings.setUsuario(usuario);
        return settings;
    }

    public UserSettingsResponseDTO mapToDto(UserSettings settings) {
        UserSettingsResponseDTO dto = new UserSettingsResponseDTO();
        dto.setId(settings.getId());
        dto.setTheme(settings.getTheme().toString());
        dto.setNotificaciones(settings.getNotificaciones());
        dto.setFormatoHora(settings.getFormatoHora());
        dto.setInicioPlanner(settings.getInicioPlanner());
        dto.setFinPlanner(settings.getFinPlanner());
        dto.setPrimerDia(settings.getPrimerDia());
        return dto;
    }
}
