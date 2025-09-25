package aktech.planificador.Service.core;

import java.util.List;
import java.util.stream.Collectors;

import aktech.planificador.Repository.core.UsuarioRepository;
import aktech.planificador.Dto.usuarios.UsuarioResponseDto;
import aktech.planificador.Model.core.Usuario;

public class AdminService {

    public final UsuarioRepository usuarioRepository;

    public AdminService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // lista todos los usuarios
    public List<UsuarioResponseDto> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuario -> {
                    UsuarioResponseDto dto = new UsuarioResponseDto();
                    dto.setId(usuario.getId());
                    dto.setEmail(usuario.getEmail());
                    dto.setNombre(usuario.getNombre());
                    dto.setApellido(usuario.getApellido());
                    dto.setRol(usuario.getRol() != null ? usuario.getRol().toString() : null);
                    dto.setActivo(usuario.getActivo());
                    dto.setCambiarPass(usuario.getCambiarPass());
                    dto.setEmailVerified(usuario.getEmailVerified());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
