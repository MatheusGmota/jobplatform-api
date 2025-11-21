package br.com.api.workTree.controllers;

import br.com.api.workTree.domain.dtos.LoginRequestDTO;
import br.com.api.workTree.domain.dtos.LoginResponseDTO;
import br.com.api.workTree.domain.dtos.RegisterRequestDTO;
import br.com.api.workTree.domain.dtos.RegisterResponseDTO;
import br.com.api.workTree.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginRequestDTO data) {
        LoginResponseDTO login = userService.login(data);
        return ResponseEntity.status(201).body(login);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody @Valid RegisterRequestDTO data) {
        RegisterResponseDTO register = userService.register(data);
        return ResponseEntity.ok(register);
    }
}
