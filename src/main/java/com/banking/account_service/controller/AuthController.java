package com.banking.account_service.controller;

import com.banking.account_service.model.User;
import com.banking.account_service.service.UserService;
import com.banking.account_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // Endpoint para registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        String rawPassword = user.getPassword().trim();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        User savedUser = userService.saveUser(user);
        logger.info("Usuario registrado: {} con contraseña original: {} y contraseña cifrada: {}", user.getUsername(), rawPassword, encodedPassword);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Endpoint para autenticar un usuario y generar el JWT
    @PostMapping("/authenticate")
    public ResponseEntity<String> createAuthenticationToken(@RequestBody Map<String, String> request) {
        String username = request.get("username").trim();
        String password = request.get("password").trim();

        logger.info("Autenticando usuario: {}", username);

        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.info("Usuario encontrado: {}", user.getUsername());
            logger.info("Contraseña proporcionada: {}, Contraseña almacenada: {}", password, user.getPassword());
            try {
                boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
                logger.info("¿La contraseña coincide? {}", passwordMatches);
                if (passwordMatches) {
                    logger.info("Contraseña válida para usuario: {}", username);
                    String token = jwtUtil.generateToken(username);
                    return ResponseEntity.ok(token);
                } else {
                    logger.warn("Contraseña incorrecta para usuario: {}", username);
                }
            } catch (Exception e) {
                logger.error("Error al verificar la contraseña: {}", e.getMessage());
            }
        } else {
            logger.warn("Usuario no encontrado: {}", username);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }
}