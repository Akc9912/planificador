package aktech.planificador.Controller;

import aktech.planificador.DTO.auth.ChangePasswordRequestDTO;
import aktech.planificador.DTO.auth.ChangePasswordResponseDTO;
import aktech.planificador.DTO.auth.LoginRequestDTO;
import aktech.planificador.DTO.auth.LoginResponseDTO;
import aktech.planificador.DTO.auth.RegisterRequestDTO;
import aktech.planificador.DTO.auth.RegisterResponseDTO;
import aktech.planificador.Service.auth.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public RegisterResponseDTO register(@RequestBody RegisterRequestDTO request) {
        return authService.register(request);
    }

    @PutMapping("/change-password")
    public ChangePasswordResponseDTO changePassword(@RequestBody ChangePasswordRequestDTO request) {
        return authService.changePassword(request);
    }
}
