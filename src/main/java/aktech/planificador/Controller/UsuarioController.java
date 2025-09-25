package aktech.planificador.Controller;

import aktech.planificador.Dto.materia.DashboardDataDto;
import aktech.planificador.Dto.materia.StatsMateriaDto;
import aktech.planificador.Dto.usuarios.UserSettingsResponseDto;
import aktech.planificador.Dto.usuarios.UsuarioRequestDto;
import aktech.planificador.Dto.usuarios.UsuarioResponseDto;
import aktech.planificador.Service.core.UserSettingsService;
import aktech.planificador.Service.core.UsuarioService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UserSettingsService userSettingsService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UserSettingsService userSettingsService) {
        this.usuarioService = usuarioService;
        this.userSettingsService = userSettingsService;
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

    // datos dashboard
    @GetMapping(path = "/dashboard/{id}")
    public DashboardDataDto getDatosDashboard(@PathVariable Integer id) {
        return usuarioService.obtenerDatosDashboard(id);
    }

    // stats materias
    @GetMapping(path = "/{id}/stats-materias")
    public StatsMateriaDto getStatsMaterias(@PathVariable Integer id) {
        return usuarioService.obtenerStatsMateria(id);
    }

    // actualizar settings
    @PutMapping(path = "/{id}/settings")
    public UserSettingsResponseDto updateSettings(@PathVariable Integer id, @RequestBody UserSettingsResponseDto dto) {
        return userSettingsService.updateSettings(id, dto);
    }
}
