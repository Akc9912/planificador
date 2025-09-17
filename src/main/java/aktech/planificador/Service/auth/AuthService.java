
package aktech.planificador.Service.auth;

import org.springframework.stereotype.Service;

import aktech.planificador.DTO.auth.ChangePasswordRequestDTO;
import aktech.planificador.DTO.auth.ChangePasswordResponseDTO;
import aktech.planificador.DTO.auth.LoginRequestDTO;
import aktech.planificador.DTO.auth.LoginResponseDTO;
import aktech.planificador.DTO.auth.RegisterRequestDTO;
import aktech.planificador.DTO.auth.RegisterResponseDTO;
import aktech.planificador.DTO.usuarios.UserSettingsResponseDTO;
import aktech.planificador.DTO.usuarios.UsuarioResponseDTO;
import aktech.planificador.Model.enums.Rol;
import aktech.planificador.Repository.core.UserSettingsRepository;
import aktech.planificador.Repository.core.UsuarioRepository;
import aktech.planificador.Service.core.UserSettingsService;
import aktech.planificador.Service.core.UsuarioService;
import aktech.planificador.Service.security.JwtService;
import aktech.planificador.Model.core.NormalUser;
import aktech.planificador.Model.core.UserSettings;
import aktech.planificador.Model.core.Usuario;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final UserSettingsRepository userSettingsRepository;
    private final UsuarioService usuarioService;
    private final UserSettingsService userSettingsService;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService,
            UserSettingsRepository userSettingsRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.userSettingsRepository = userSettingsRepository;
        this.usuarioService = usuarioService;
        this.userSettingsService = new UserSettingsService(userSettingsRepository);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        // Validar campos requeridos
        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Email y contraseña son obligatorios");
        }

        // Buscar el usuario por email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());
        if (!usuarioOpt.isPresent()) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        Usuario usuario = usuarioOpt.get();

        // Usuario inactivo
        if (usuario.getActivo() != null && !usuario.getActivo()) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Email no verificado
        if (usuario.getEmailVerified() != null && !usuario.getEmailVerified()) {
            throw new IllegalArgumentException("Debe verificar su email antes de iniciar sesión.");
        }

        // Verificar la contraseña hasheada
        if (!BCrypt.checkpw(request.getPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        UsuarioResponseDTO userDto = usuarioService.mapToDto(usuario);
        String token = jwtService.generateToken(usuario);
        // String refreshToken = null; // Aquí podrías generar un refresh token si tu
        // lógica lo requiere

        // busco la configuración del usuario
        UserSettings userSettings = userSettingsRepository.findByUsuarioId(usuario.getId());
        UserSettingsResponseDTO settingsDto;
        if (userSettings != null) {
            settingsDto = userSettingsService.mapToDto(userSettings);
        } else {
            // Si no hay configuración, usar la función de defaults y mapear
            UserSettings defaultSettings = userSettingsService.setDefaultSettings(usuario);
            settingsDto = userSettingsService.mapToDto(defaultSettings);
        }

        // construyo y retorno la respuesta de forma clara
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        // response.setRefreshToken(refreshToken);
        response.setUsuario(userDto);
        response.setUserSettings(settingsDto);
        return response;
    }

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        // Validar campos requeridos
        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty() ||
                request.getNombre() == null || request.getNombre().isEmpty() ||
                request.getApellido() == null || request.getApellido().isEmpty()) {
            return new RegisterResponseDTO("Faltan datos obligatorios para el registro", false);
        }

        // Verificar que el mail no esté registrado (solo si está activo)
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(request.getEmail());
        if (usuarioExistente.isPresent()
                && (usuarioExistente.get().getActivo() == null || usuarioExistente.get().getActivo())) {
            return new RegisterResponseDTO("El email ya está registrado", false);
        }

        // Hash de la contraseña antes de guardar
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        // Crear un nuevo usuario
        NormalUser nuevoUsuario = new NormalUser(request.getEmail(), hashedPassword, request.getNombre(),
                request.getApellido());

        try {
            // Guardar el nuevo usuario en la base de datos
            usuarioRepository.save(nuevoUsuario);
            // Crear y guardar configuración por defecto
            UserSettings defaultSettings = userSettingsService.setDefaultSettings(nuevoUsuario);
            userSettingsRepository.save(defaultSettings);
            return new RegisterResponseDTO("Usuario registrado con éxito", true);
        } catch (Exception e) {
            return new RegisterResponseDTO("Error al registrar usuario: " + e.getMessage(), false);
        }
    }


    public ChangePasswordResponseDTO changePassword(ChangePasswordRequestDTO request) {
        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getOldPassword() == null || request.getOldPassword().isEmpty() ||
                request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return new ChangePasswordResponseDTO("Faltan datos obligatorios", false);
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());
        if (!usuarioOpt.isPresent()) {
            return new ChangePasswordResponseDTO("Usuario no encontrado", false);
        }
        Usuario usuario = usuarioOpt.get();
        if (!BCrypt.checkpw(request.getOldPassword(), usuario.getPasswordHash())) {
            return new ChangePasswordResponseDTO("Contraseña actual incorrecta", false);
        }
        String hashedNewPassword = BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt());
        usuario.setPasswordHash(hashedNewPassword);
        usuarioRepository.save(usuario);
        return new ChangePasswordResponseDTO("Contraseña cambiada con éxito", true);
    }
}
