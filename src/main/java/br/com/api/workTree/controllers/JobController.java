package br.com.api.workTree.controllers;

import br.com.api.workTree.domain.dtos.JobRequestDTO;
import br.com.api.workTree.domain.dtos.JobResponseDTO;
import br.com.api.workTree.services.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin("*")
public class JobController {

    @Autowired
    private JobService service;

    @GetMapping
    public ResponseEntity<Object> getAll(Pageable pageable) {
        List<JobResponseDTO> response = service.obterTodos(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long jobId) {
        JobResponseDTO response = service.obterPorId(jobId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody JobRequestDTO dto,
            @RequestParam(value = "company-id", required = true) Long createdByUserId) {
        JobResponseDTO job = service.save(dto, createdByUserId);
        return ResponseEntity.status(201).body(job);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody JobRequestDTO dto) {
        JobResponseDTO atualizar = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizar);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long jobId) {
        service.deletar(jobId);
        return ResponseEntity.ok().body("Deletado com sucesso");
    }
}
