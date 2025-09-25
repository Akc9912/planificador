package aktech.planificador.Service.core;

import org.springframework.stereotype.Service;

import aktech.planificador.Model.enums.Theme;
import aktech.planificador.Repository.core.UserSettingsRepository;
import aktech.planificador.Dto.usuarios.UserSettingsResponseDto;
import aktech.planificador.Model.core.UserSettings;
import aktech.planificador.Model.core.Usuario;
import aktech.planificador.Model.enums.DiaSemana;

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

    public UserSettingsResponseDto mapToDto(UserSettings settings) {
        UserSettingsResponseDto dto = new UserSettingsResponseDto();
        dto.setId(settings.getId());
        dto.setTheme(settings.getTheme().toString());
        dto.setNotificaciones(settings.getNotificaciones());
        dto.setFormatoHora(settings.getFormatoHora());
        dto.setInicioPlanner(settings.getInicioPlanner());
        dto.setFinPlanner(settings.getFinPlanner());
        dto.setPrimerDia(settings.getPrimerDia());
        return dto;
    }

    // actualizar settings
    public UserSettingsResponseDto updateSettings(Integer userId, UserSettingsResponseDto dto) {
        UserSettings settings = userSettingsRepository.findByUsuarioId(userId);
        if (settings == null) {
            throw new RuntimeException("Configuración de usuario no encontrada");
        }
        if (dto.getTheme() != null) {
            settings.setTheme(Theme.valueOf(dto.getTheme()));
        }
        if (dto.getNotificaciones() != null) {
            settings.setNotificaciones(dto.getNotificaciones());
        }
        if (dto.getFormatoHora() != null) {
            settings.setFormatoHora(dto.getFormatoHora());
        }
        if (dto.getInicioPlanner() != null) {
            settings.setInicioPlanner(dto.getInicioPlanner());
        }
        if (dto.getFinPlanner() != null) {
            settings.setFinPlanner(dto.getFinPlanner());
        }
        if (dto.getPrimerDia() != null) {
            settings.setPrimerDia(dto.getPrimerDia());
        }
        userSettingsRepository.save(settings);
        return mapToDto(settings);
    }
}
