package pe.edu.sst.backend.modules.trabajadores.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.sst.backend.modules.trabajadores.dto.request.ActualizarTrabajadorRequest;
import pe.edu.sst.backend.modules.trabajadores.dto.request.CrearTrabajadorRequest;
import pe.edu.sst.backend.modules.trabajadores.dto.response.MaestraResponse;
import pe.edu.sst.backend.modules.trabajadores.dto.response.TrabajadorResponse;
import pe.edu.sst.backend.modules.trabajadores.service.TrabajadorService;

import java.util.List;

import pe.edu.sst.backend.config.constants.ApiPaths;

@RestController
@RequestMapping(ApiPaths.TRABAJADORES)
@RequiredArgsConstructor
public class TrabajadorController {

    private final TrabajadorService trabajadorService;

    @PostMapping
    public ResponseEntity<TrabajadorResponse> crear(@Valid @RequestBody CrearTrabajadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trabajadorService.crear(request));
    }

    @GetMapping
    public ResponseEntity<Page<TrabajadorResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(trabajadorService.listarPaginado(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrabajadorResponse>> buscarPorSegmento(
            @RequestParam String segment,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(trabajadorService.buscarPorSegmento(segment, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrabajadorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(trabajadorService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrabajadorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarTrabajadorRequest request) {
        return ResponseEntity.ok(trabajadorService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        trabajadorService.cambiarEstado(id, estado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/catalogos/sedes")
    public ResponseEntity<List<MaestraResponse>> listarSedes() {
        return ResponseEntity.ok(trabajadorService.listarSedes());
    }

    @GetMapping("/catalogos/cargos")
    public ResponseEntity<List<MaestraResponse>> listarCargos() {
        return ResponseEntity.ok(trabajadorService.listarCargos());
    }
}
