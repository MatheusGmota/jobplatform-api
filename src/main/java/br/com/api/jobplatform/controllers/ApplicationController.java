package br.com.api.jobplatform.controllers;

import br.com.api.jobplatform.domain.dtos.ApplicationRequestDTO;
import br.com.api.jobplatform.domain.dtos.ApplicationResponseDTO;
import br.com.api.jobplatform.services.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/application")
@CrossOrigin("*")
public class ApplicationController {

    @Autowired
    private ApplicationService service;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long id){
        ApplicationResponseDTO response = service.obterPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply")
    public ResponseEntity<Object> apply(@Valid @RequestBody ApplicationRequestDTO dto) {
        ApplicationResponseDTO app = service.apply(dto);
        return ResponseEntity.status(201).body(app);
    }
}
