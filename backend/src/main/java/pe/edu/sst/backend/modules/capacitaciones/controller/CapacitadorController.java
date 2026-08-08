package pe.edu.sst.backend.modules.capacitaciones.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitadorRequest;
import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitadorResponse;
import pe.edu.sst.backend.modules.capacitaciones.service.CapacitadorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/capacitadores")
@RequiredArgsConstructor
public class CapacitadorController {

    private final CapacitadorService capacitadorService;

    @GetMapping
    public ResponseEntity<List<CapacitadorResponse>> listarActivos() {
        return ResponseEntity.ok(capacitadorService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CapacitadorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(capacitadorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CapacitadorResponse> crear(@Valid @RequestBody CapacitadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(capacitadorService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CapacitadorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CapacitadorRequest request) {
        return ResponseEntity.ok(capacitadorService.actualizar(id, request));
    }
}
