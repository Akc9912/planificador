package aktech.planificador.Service.core;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import aktech.planificador.Dto.materia.DashboardDataDto;
import aktech.planificador.Dto.materia.MateriaPlannerResponseDto;
import aktech.planificador.Dto.materia.StatsMateriaDto;
import aktech.planificador.Dto.usuarios.UsuarioRequestDto;
import aktech.planificador.Dto.usuarios.UsuarioResponseDto;
import aktech.planificador.Model.core.Materia;
import aktech.planificador.Model.core.NormalUser;
import aktech.planificador.Model.core.Usuario;
import aktech.planificador.Model.enums.DiaSemana;
import aktech.planificador.Model.enums.EstadoMateria;
import aktech.planificador.Model.historial.HorarioPorMateria;
import aktech.planificador.Repository.core.MateriaRepository;
import aktech.planificador.Repository.core.UsuarioRepository;
import aktech.planificador.Repository.historial.HorarioPorMateriaRepository;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final MateriaRepository materiaRepository;
    private final HorarioPorMateriaRepository horarioRepository;
    private final MateriaService materiaService;

    public UsuarioService(UsuarioRepository usuarioRepository, MateriaRepository materiaRepository,
            HorarioPorMateriaRepository horarioRepository, MateriaService materiaService) {
        this.usuarioRepository = usuarioRepository;
        this.materiaRepository = materiaRepository;
        this.horarioRepository = horarioRepository;
        this.materiaService = materiaService;
    }

    public UsuarioResponseDto mapToDto(Usuario user) {
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNombre(user.getNombre());
        dto.setApellido(user.getApellido());
        dto.setRol(user.getRol().toString());
        dto.setActivo(user.getActivo());
        dto.setCambiarPass(user.getCambiarPass());
        dto.setEmailVerified(user.getEmailVerified());
        return dto;
    }

    // crear usuarios (para admin)

    @org.springframework.transaction.annotation.Transactional
    public UsuarioResponseDto createUsuario(UsuarioRequestDto dto) {
        if (dto.getEmail() == null || dto.getEmail().isEmpty() ||
                dto.getNombre() == null || dto.getNombre().isEmpty() ||
                dto.getApellido() == null || dto.getApellido().isEmpty()) {
            throw new IllegalArgumentException("Faltan campos obligatorios");
        }

        // Verificar si el email ya está en uso
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        // Por defecto, crear como NormalUser
        Usuario newUser = new NormalUser(
                dto.getEmail(),
                null, // password no viene en el DTO
                dto.getNombre(),
                dto.getApellido());
        newUser.setActivo(true);
        newUser.setCambiarPass(true);
        newUser.setEmailVerified(true);

        Usuario savedUser = usuarioRepository.save(newUser);
        return mapToDto(savedUser);
    }

    // crear usuario (para auth, registro por el mismo usuario)
    @org.springframework.transaction.annotation.Transactional
    public UsuarioResponseDto createUsuario(Usuario user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPasswordHash() == null || user.getPasswordHash().isEmpty() ||
                user.getNombre() == null || user.getNombre().isEmpty() ||
                user.getApellido() == null || user.getApellido().isEmpty()) {
            throw new IllegalArgumentException("Faltan campos obligatorios");
        }

        // Verificar si el email ya está en uso
        if (usuarioRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        user.setActivo(true); // Por defecto, el usuario se crea como activo
        user.setCambiarPass(false); // por registro propio, no necesita cambiar la pass
        user.setEmailVerified(false); // por ahora hasta implementar verificación de email

        Usuario savedUser = usuarioRepository.save(user);
        return mapToDto(savedUser);
    }

    // actualizar usuario para normal y admin
    @org.springframework.transaction.annotation.Transactional
    public UsuarioResponseDto updateUsuario(Integer id, UsuarioRequestDto dto) {
        Usuario existingUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            // Verificar si el nuevo email ya está en uso por otro usuario
            usuarioRepository.findByEmail(dto.getEmail()).ifPresent(userWithEmail -> {
                if (!userWithEmail.getId().equals(id)) {
                    throw new IllegalArgumentException("El email ya está en uso por otro usuario");
                }
            });
            existingUser.setEmail(dto.getEmail());
        }
        if (dto.getNombre() != null && !dto.getNombre().isEmpty()) {
            existingUser.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null && !dto.getApellido().isEmpty()) {
            existingUser.setApellido(dto.getApellido());
        }
        usuarioRepository.save(existingUser);
        return mapToDto(existingUser);
    }

    // stats para el dashboard
    public DashboardDataDto obtenerDatosDashboard(Integer usuarioId) {
        DashboardDataDto dto = new DashboardDataDto();
        dto.setMateriasActivas(materiaService.contarMateriasActivasPorUsuario(usuarioId));
        dto.setHorasSemanales(materiaService.contarHorasSemanalesDeMateriasActivas(usuarioId));
        dto.setProximasMaterias(materiaService.obtenerProximasMateriasDelDia(usuarioId));
        return dto;
    }

    // stats para seccion materias
    public StatsMateriaDto obtenerStatsMateria(Integer usuarioId) {
        StatsMateriaDto dto = new StatsMateriaDto();
        dto.setMateriasActivas(materiaService.contarMateriasActivasPorUsuario(usuarioId));
        dto.setMateriasTotales(materiaService.contarTotalMateriasPorUsuario(usuarioId));
        dto.setPromedio(materiaService.promedioCalificacionesDeMaterias(usuarioId));
        return dto;
    }

    // baja usuario (solo desactivar)
    @Transactional
    public boolean deleteUsuario(Integer id) {
        Usuario existingUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));
        existingUser.setActivo(false);
        usuarioRepository.save(existingUser);
        return true;
    }
}
