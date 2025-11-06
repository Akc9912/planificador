package aktech.planificador.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import aktech.planificador.Dto.evento.EventoRequestDto;
import aktech.planificador.Dto.evento.EventoResponseDto;
import aktech.planificador.Service.core.EventoService;

@RestController
@RequestMapping("/eventos")
public class EventoController {
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping("/crear")
    public EventoResponseDto crearEvento(@RequestBody EventoRequestDto request) {
        return eventoService.crearEvento(request);
    }

    @PutMapping("/modificar/{id}")
    public EventoResponseDto modificarEvento(@PathVariable Integer id, @RequestBody EventoRequestDto request) {
        return eventoService.modificarEvento(id, request);
    }

    @DeleteMapping("/eliminar/{id}")
    public void eliminarEvento(@PathVariable Integer id, @RequestParam Integer usuarioId) {
        eventoService.eliminarEvento(id, usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<EventoResponseDto> obtenerEventosPorUsuario(@PathVariable Integer usuarioId) {
        return eventoService.obtenerEventosPorUsuario(usuarioId);
    }

    @GetMapping("/materia/{materiaId}")
    public List<EventoResponseDto> obtenerEventosPorMateria(@PathVariable Integer materiaId) {
        return eventoService.obtenerEventosPorMateria(materiaId);
    }
}
