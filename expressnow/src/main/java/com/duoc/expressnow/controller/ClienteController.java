package com.duoc.expressnow.controller;

import com.duoc.expressnow.dto.ClienteResponseDTO;
import com.duoc.expressnow.model.ClienteModel;
import com.duoc.expressnow.repository.ClienteRepository;
import com.duoc.expressnow.seguridad.ClienteSeguridad;
import com.duoc.expressnow.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private ClienteSeguridad seguridad;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ClienteResponseDTO aDto(ClienteModel c) {
        return new ClienteResponseDTO(c.getId(), c.getUsername(), c.getEmail());
    }

    @PostMapping("/registrar")
    public ResponseEntity<ClienteResponseDTO> registrar(@RequestBody ClienteModel cliente) {
        ClienteModel guardado = service.registrar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(aDto(guardado));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ClienteModel loginRequest) {
        // 1. Validar que el username no venga nulo o vacío en el payload
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El campo 'username' es obligatorio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // 2. Buscar al usuario en la base de datos por su username
        ClienteModel u = repository.findByUsername(loginRequest.getUsername());

        // 3. Validar si el usuario existe y si la contraseña coincide con el hash bCrypt
        if (u != null && passwordEncoder.matches(loginRequest.getPassword(), u.getPassword())) {
            // Generar el token JWT utilizando el username verificado (nunca nulo)
            String token = seguridad.generarToken(u.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("access_token", token);
            response.put("token_type", "Bearer");

            return ResponseEntity.ok(response);
        }

        // 4. Retornar 401 Unauthorized si las credenciales fallan
        Map<String, String> error = new HashMap<>();
        error.put("error", "Credenciales incorrectas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(usuario -> ResponseEntity.ok(aDto(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }
}
