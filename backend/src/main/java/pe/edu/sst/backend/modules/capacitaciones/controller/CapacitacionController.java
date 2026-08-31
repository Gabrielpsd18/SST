package pe.edu.sst.backend.modules.capacitaciones.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.sst.backend.config.constants.ApiPaths;
import pe.edu.sst.backend.modules.capacitaciones.dto.CrearCapacitacionRequest;
import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitacionResponse;
import pe.edu.sst.backend.modules.capacitaciones.service.CapacitacionService;

@RestController
@RequestMapping(ApiPaths.CAPACITACIONES)
@RequiredArgsConstructor
public class CapacitacionController {

    private final CapacitacionService capacitacionService;

    @GetMapping
    public ResponseEntity<Page<CapacitacionResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(capacitacionService.listarPaginado(pageable));
    }

    @GetMapping("/mobile")
    public ResponseEntity<Page<pe.edu.sst.backend.modules.capacitaciones.dto.CapacitacionMobileResponse>> listarParaMobile(
            @RequestParam(required = false, defaultValue = "TODOS") String filtro,
            Pageable pageable) {
        return ResponseEntity.ok(capacitacionService.listarParaMobile(filtro, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CapacitacionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(capacitacionService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CapacitacionResponse> programar(@Valid @RequestBody CrearCapacitacionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth != null ? auth.getName() : "admin@sst.com";
        return ResponseEntity.status(HttpStatus.CREATED).body(capacitacionService.programar(request, email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CapacitacionResponse> actualizar(@PathVariable Long id, @Valid @RequestBody CrearCapacitacionRequest request) {
        return ResponseEntity.ok(capacitacionService.actualizar(id, request));
    }
}
