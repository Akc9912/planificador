package aktech.planificador.Controller;

import aktech.planificador.Dto.auth.ChangePasswordRequestDto;
import aktech.planificador.Dto.auth.ChangePasswordResponseDto;
import aktech.planificador.Dto.auth.LoginRequestDto;
import aktech.planificador.Dto.auth.LoginResponseDto;
import aktech.planificador.Dto.auth.RegisterRequestDto;
import aktech.planificador.Dto.auth.RegisterResponseDto;
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
    public RegisterResponseDto register(@RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }

    @PutMapping("/change-password")
    public ChangePasswordResponseDto changePassword(@RequestBody ChangePasswordRequestDto request) {
        return authService.changePassword(request);
    }
}
