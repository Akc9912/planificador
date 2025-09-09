package aktech.planificador.Service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;

import aktech.planificador.Controller.RegisterRequestDTO;
import aktech.planificador.Controller.RegisterResponseDTO;
import aktech.planificador.DTO.auth.LoginRequestDto;
import aktech.planificador.DTO.auth.LoginResponseDto;
import aktech.planificador.DTO.core.UsuarioResponseDto;
import aktech.planificador.Model.core.Usuario;
import aktech.planificador.Model.enums.Rol;
import aktech.planificador.Repository.UsuarioRepository;
import aktech.planificador.Service.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    // login y register

    public LoginResponseDto login(LoginRequestDto request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean passwordMatches = false;
        if (usuario != null) {
            passwordMatches = passwordEncoder.matches(request.getPassword(), usuario.getPassword());
        } else {
            // Ejecutar una verificación dummy para mantener el mismo tiempo de respuesta
            passwordEncoder.matches(request.getPassword(), "requesta");
        }

        if (usuario == null || !usuario.isActive()) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }

        if (!passwordMatches) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(usuario);
        UsuarioResponseDto usuarioDto = new UsuarioResponseDto();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setEmail(usuario.getEmail());
        usuarioDto.setNombre(usuario.getNombre());
        usuarioDto.setApellido(usuario.getApellido());
        usuarioDto.setActive(usuario.isActive());
        usuarioDto.setCambiarPass(usuario.isCambiarPass());
        if (usuario.getRol() != null) {
            usuarioDto.setRole(usuario.getRol().name());
        } else {
            usuarioDto.setRole("NO_ROLE");
        }

        return new LoginResponseDto(token, usuarioDto);
    }

    

    // Crear un nuevo usuario (formulario de registro)
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está en uso");
        }
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setApellido(request.getApellido());
        // Por defecto, el usuario está activo y no necesita cambiar la contraseña
        nuevoUsuario.setActive(true);
        nuevoUsuario.setCambiarPass(false);
        // Asigno rol por defecto si el usuario se registra por el formulario
        nuevoUsuario.setRol(Rol.USUARIO);

        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        return new RegisterResponseDTO("Registro exitoso", nuevoUsuario.getEmail(), nuevoUsuario.getNombre(), nuevoUsuario.getApellido());
    }
}
