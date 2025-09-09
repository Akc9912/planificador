package aktech.planificador.Config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import aktech.planificador.Model.core.Usuario;
import aktech.planificador.Model.enums.Rol;
import aktech.planificador.Repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor para inyección de dependencias
    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            logger.info("[DataInitializer] Inicio de configuración inicial de datos");

            String emailAdmin = "admin@example.com";
            String emailUser = "user@example.com";
            String passwordAdmin = "admin123";
            String passwordUser = "user123";

            // Verificar si el usuario admin ya existe
            if (usuarioRepository.findByEmail(emailAdmin).isEmpty()) {
                // Crear el usuario admin
                Usuario admin = new Usuario();
                admin.setEmail(emailAdmin);
                admin.setPassword(passwordEncoder.encode(passwordAdmin));
                admin.setRol(Rol.ADMIN);
                admin.setActive(true);
                admin.setCambiarPass(false);
                admin.setNombre("Admin");
                admin.setApellido("User");
                usuarioRepository.save(admin);
                logger.info("[DataInitializer] Usuario admin creado: {}", emailAdmin);
            } else {
                logger.info("[DataInitializer] El usuario admin ya existe: {}", emailAdmin);
            }


            // Verificar si el usuario normal ya existe

            if (usuarioRepository.findByEmail(emailUser).isEmpty()) {
                // Crear el usuario normal
                Usuario user = new Usuario();
                user.setEmail(emailUser);
                user.setPassword(passwordEncoder.encode(passwordUser));
                user.setRol(Rol.USUARIO);
                user.setActive(true);
                user.setCambiarPass(false);
                user.setNombre("Normal");
                user.setApellido("User");
                usuarioRepository.save(user);
                logger.info("[DataInitializer] Usuario normal creado: {}", emailUser);
            } else {
                logger.info("[DataInitializer] El usuario normal ya existe: {}", emailUser);
            }

        } catch (Exception e) {
            logger.error("[DataInitializer] Error durante la inicialización de datos: {}", e.getMessage());
        }
    }

}
