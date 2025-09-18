
package aktech.planificador.Service.auth;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aktech.planificador.DTO.auth.ChangePasswordRequestDto;
import aktech.planificador.DTO.auth.ChangePasswordResponseDto;
import aktech.planificador.DTO.auth.LoginRequestDto;
import aktech.planificador.DTO.auth.LoginResponseDto;
import aktech.planificador.DTO.auth.RegisterRequestDto;
import aktech.planificador.DTO.auth.RegisterResponseDto;
import aktech.planificador.DTO.usuarios.UserSettingsResponseDto;
import aktech.planificador.DTO.usuarios.UsuarioResponseDto;
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
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
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

    public LoginResponseDto login(LoginRequestDto request) {
        try {
            // Validar campos requeridos
            if (request.getEmail() == null || request.getEmail().isEmpty() ||
                    request.getPassword() == null || request.getPassword().isEmpty()) {
                logger.warn("Login fallido: Email y contraseña son obligatorios");
                throw new IllegalArgumentException("Email y contraseña son obligatorios");
            }

            // Buscar el usuario por email
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());
            if (!usuarioOpt.isPresent()) {
                logger.warn("Login fallido: Credenciales inválidas para email {}", request.getEmail());
                throw new IllegalArgumentException("Credenciales inválidas");
            }
            Usuario usuario = usuarioOpt.get();

            // Usuario inactivo
            if (usuario.getActivo() != null && !usuario.getActivo()) {
                logger.warn("Login fallido: Usuario inactivo para email {}", request.getEmail());
                throw new IllegalArgumentException("Credenciales inválidas");
            }

            // Email no verificado
            if (usuario.getEmailVerified() != null && !usuario.getEmailVerified()) {
                logger.warn("Login fallido: Email no verificado para email {}", request.getEmail());
                throw new IllegalArgumentException("Debe verificar su email antes de iniciar sesión.");
            }

            // Verificar la contraseña hasheada
            if (!BCrypt.checkpw(request.getPassword(), usuario.getPasswordHash())) {
                logger.warn("Login fallido: Contraseña incorrecta para email {}", request.getEmail());
                throw new IllegalArgumentException("Credenciales inválidas");
            }

            UsuarioResponseDto userDto = usuarioService.mapToDto(usuario);
            String token = jwtService.generateToken(usuario);
            // String refreshToken = null; // Aquí podrías generar un refresh token si tu
            // lógica lo requiere

            // busco la configuración del usuario
            UserSettings userSettings = userSettingsRepository.findByUsuarioId(usuario.getId());
            UserSettingsResponseDto settingsDto;
            if (userSettings != null) {
                settingsDto = userSettingsService.mapToDto(userSettings);
            } else {
                // Si no hay configuración, usar la función de defaults y mapear
                UserSettings defaultSettings = userSettingsService.setDefaultSettings(usuario);
                settingsDto = userSettingsService.mapToDto(defaultSettings);
            }

            // construyo y retorno la respuesta de forma clara
            LoginResponseDto response = new LoginResponseDto();
            response.setToken(token);
            // response.setRefreshToken(refreshToken);
            response.setUsuario(userDto);
            response.setUserSettings(settingsDto);
            logger.info("Login exitoso para email {}", request.getEmail());
            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado en login para email {}: {}", request.getEmail(), e.getMessage(), e);
            throw new IllegalArgumentException("Error inesperado en login: " + e.getMessage());
        }
    }

    public RegisterResponseDto register(RegisterRequestDto request) {
        // Validar campos requeridos
        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty() ||
                request.getNombre() == null || request.getNombre().isEmpty() ||
                request.getApellido() == null || request.getApellido().isEmpty()) {
            return new RegisterResponseDto("Faltan datos obligatorios para el registro", false);
        }

        // Verificar que el mail no esté registrado (solo si está activo)
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(request.getEmail());
        if (usuarioExistente.isPresent()
                && (usuarioExistente.get().getActivo() == null || usuarioExistente.get().getActivo())) {
            return new RegisterResponseDto("El email ya está registrado", false);
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
            return new RegisterResponseDto("Usuario registrado con éxito", true);
        } catch (Exception e) {
            return new RegisterResponseDto("Error al registrar usuario: " + e.getMessage(), false);
        }
    }


    public ChangePasswordResponseDto changePassword(ChangePasswordRequestDto request) {
        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getOldPassword() == null || request.getOldPassword().isEmpty() ||
                request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return new ChangePasswordResponseDto("Faltan datos obligatorios", false);
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());
        if (!usuarioOpt.isPresent()) {
            return new ChangePasswordResponseDto("Usuario no encontrado", false);
        }
        Usuario usuario = usuarioOpt.get();
        if (!BCrypt.checkpw(request.getOldPassword(), usuario.getPasswordHash())) {
            return new ChangePasswordResponseDto("Contraseña actual incorrecta", false);
        }
        String hashedNewPassword = BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt());
        usuario.setPasswordHash(hashedNewPassword);
        usuarioRepository.save(usuario);
        return new ChangePasswordResponseDto("Contraseña cambiada con éxito", true);
    }
}
