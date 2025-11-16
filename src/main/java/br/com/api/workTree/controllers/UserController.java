package br.com.api.workTree.controllers;

import br.com.api.workTree.domain.dtos.UserRequestDTO;
import br.com.api.workTree.domain.dtos.UserResponseDTO;
import br.com.api.workTree.services.UserService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
public class UserController {

    @Autowired
    UserService service;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Long id) {
        UserResponseDTO response = this.service.obterPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> put(@Valid @RequestBody UserRequestDTO user, @PathParam("id") Long id) {
        UserResponseDTO editar = this.service.editar(user, id);
        return ResponseEntity.ok(editar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        this.service.delete(id);
        return ResponseEntity.ok("Deletado com sucesso");
    }
}
