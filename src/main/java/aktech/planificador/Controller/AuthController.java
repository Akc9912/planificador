package aktech.planificador.Controller;


import aktech.planificador.DTO.auth.LoginRequestDto;
import aktech.planificador.DTO.auth.LoginResponseDto;
import aktech.planificador.Service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public RegisterResponseDTO register(@RequestBody RegisterRequestDTO request) {
        return authService.register(request);
    }
}
