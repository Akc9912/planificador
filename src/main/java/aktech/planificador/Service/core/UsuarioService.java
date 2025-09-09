package aktech.planificador.Service.core;

import aktech.planificador.DTO.auth.UserRegisterRequestDto;
import aktech.planificador.Model.core.Usuario;
import aktech.planificador.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    // Usar los DTO para las respuestas y peticiones

    

    // crear un nuevo usuario (desde el admin)
    public Usuario crearUsuario(UserRegisterRequestDto request) {
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getApellido() + "123"); // contraseña por defecto si lo crea el admin
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        return usuarioRepository.save(usuario);
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtener un usuario por su ID
    public Optional<Usuario> obtenerUsuarioPorId(int id) {
        return usuarioRepository.findById(id);
    }

    // obtener un usuario por su email
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Actualizar un usuario existente
    public Optional<Usuario> actualizarUsuario(int id, UserRegisterRequestDto request) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setEmail(request.getEmail());
            usuario.setPassword(request.getPassword());
            usuario.setNombre(request.getNombre());
            usuario.setApellido(request.getApellido());
            return Optional.of(usuarioRepository.save(usuario));
        }
        return Optional.empty();
    }

    // Eliminar un usuario (baja lógica)
    public boolean eliminarUsuario(int id) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setActive(false); // Baja lógica
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

}
