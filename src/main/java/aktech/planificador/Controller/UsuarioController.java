package aktech.planificador.Controller;

import aktech.planificador.DTO.usuarios.UsuarioRequestDto;
import aktech.planificador.DTO.usuarios.UsuarioResponseDto;
import aktech.planificador.Service.core.UsuarioService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping(path = "/crear")
    public UsuarioResponseDto crearUsuario(@RequestBody UsuarioRequestDto dto) {
        return usuarioService.createUsuario(dto);
    }

    @PutMapping(path = "/actualizar/{id}")
    public UsuarioResponseDto actualizarUsuario(@PathVariable Integer id, @RequestBody UsuarioRequestDto dto) {
        return usuarioService.updateUsuario(id, dto);
    }

    @DeleteMapping(path = "/baja/{id}")
    public boolean bajaUsuario(@PathVariable Integer id) {
        return usuarioService.deleteUsuario(id);
    }
}
