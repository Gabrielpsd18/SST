package pe.edu.sst.backend.modules.inspecciones.controller;

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
import pe.edu.sst.backend.modules.inspecciones.dto.response.InspeccionResponse;
import pe.edu.sst.backend.modules.inspecciones.dto.request.CrearInspeccionRequest;
import pe.edu.sst.backend.modules.inspecciones.service.InspeccionService;

@RestController
@RequestMapping(ApiPaths.INSPECCIONES)
@RequiredArgsConstructor
public class InspeccionController {

    private final InspeccionService inspeccionService;

    @GetMapping
    public ResponseEntity<Page<InspeccionResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(inspeccionService.listarPaginado(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspeccionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inspeccionService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<InspeccionResponse> crear(@Valid @RequestBody CrearInspeccionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth != null ? auth.getName() : "admin@sst.com";
        return ResponseEntity.status(HttpStatus.CREATED).body(inspeccionService.crear(request, email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InspeccionResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody CrearInspeccionRequest request) {
        return ResponseEntity.ok(inspeccionService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        inspeccionService.cambiarEstado(id, estado);
        return ResponseEntity.noContent().build();
    }
}
