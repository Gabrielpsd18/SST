package pe.edu.sst.backend.modules.importaciones.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import pe.edu.sst.backend.config.constants.ApiPaths;
import pe.edu.sst.backend.modules.importaciones.dto.ImportResultDTO; // El nuevo DTO unificado
import pe.edu.sst.backend.modules.importaciones.dto.InvalidRowDetail; // Detalle de la fila con error
import pe.edu.sst.backend.modules.importaciones.service.ImportService;

@RestController
@RequestMapping(ApiPaths.IMPORTACIONES)
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    // Endpoint único para subir, validar, guardar los correctos y retornar los errores listos para la web
    @PostMapping(path = "/trabajadores")
    public ResponseEntity<ImportResultDTO> uploadAndImplement(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "month", required = false, defaultValue = "THIS") String month) throws Exception {
        
        ImportResultDTO res = importService.processAndImplementImport(file, month);
        return ResponseEntity.ok(res);
    }

    // Endpoint para corregir y reenviar una fila individual desde la tabla interactiva del frontend
    @PostMapping(path = "/trabajadores/retry-row")
    public ResponseEntity<Void> retryRow(@RequestBody InvalidRowDetail rowDetail) throws Exception {
        importService.processSingleRow(rowDetail);
        return ResponseEntity.noContent().build();
    }

    // Obtener errores pendientes (para mostrar siempre al entrar a la página de importaciones)
    @GetMapping(path = "/trabajadores/errors")
    public ResponseEntity<java.util.List<pe.edu.sst.backend.modules.importaciones.entity.ImportError>> getPendingErrors() {
        return ResponseEntity.ok(importService.getPendingErrors());
    }

    // Eliminar (descartar) un error pendiente por id
    @DeleteMapping(path = "/trabajadores/errors/{id}")
    public ResponseEntity<Void> deleteError(@PathVariable("id") Long id) {
        importService.deletePendingError(id);
        return ResponseEntity.noContent().build();
    }

    // Reintentar un error pendiente por id (intenta procesar la fila y si va bien, borra el error)
    @PostMapping(path = "/trabajadores/errors/{id}/retry")
    public ResponseEntity<Void> retryError(@PathVariable("id") Long id) throws Exception {
        importService.retryPendingError(id);
        return ResponseEntity.noContent().build();
    }
}
