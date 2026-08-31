package pe.edu.sst.backend.modules.documentos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.config.constants.ApiPaths;
import pe.edu.sst.backend.modules.documentos.dto.*;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoCategoria;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoEstado;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoSolicitudEstado;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoTipo;
import pe.edu.sst.backend.modules.documentos.repository.DocumentoGeneralRepository;
import pe.edu.sst.backend.modules.documentos.repository.DocumentoPersonalRepository;
import pe.edu.sst.backend.modules.documentos.service.DocumentosService;
import pe.edu.sst.backend.modules.identity.entity.Usuario;
import pe.edu.sst.backend.modules.identity.entity.repository.UsuarioRepository;
import pe.edu.sst.backend.shared.storage.FileStorageService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiPaths.DOCUMENTOS)
@RequiredArgsConstructor
public class DocumentosController {

    private final DocumentosService documentosService;
    private final FileStorageService fileStorageService;
    private final UsuarioRepository usuarioRepository;
    private final DocumentoGeneralRepository documentoGeneralRepository;
    private final DocumentoPersonalRepository documentoPersonalRepository;

    @GetMapping("/generales")
    public ResponseEntity<List<DocumentoGeneralResponse>> listarGenerales() {
        return ResponseEntity.ok(documentosService.listarGenerales());
    }

    @GetMapping("/generales/{id}")
    public ResponseEntity<DocumentoGeneralResponse> obtenerGeneral(@PathVariable Long id) {
        return ResponseEntity.ok(documentosService.obtenerGeneral(id));
    }

    @GetMapping("/{id}/signed-url")
    public ResponseEntity<Map<String, String>> generarSignedUrl(@PathVariable Long id,
                                                              @RequestParam(value = "tipo", required = false) String tipo) {
        String key = resolveFilePath(id, tipo);
        return ResponseEntity.ok(Map.of("url", fileStorageService.generateSignedUrl(key)));
    }

    @PostMapping("/generales")
    public ResponseEntity<DocumentoGeneralResponse> subirDocumentoGeneral(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "version", defaultValue = "1.0") String version,
            Authentication authentication) {

        String createdBy = authentication != null ? authentication.getName() : "admin@sst.com";
        String filePath = fileStorageService.store(file, "documentos/generales");

        DocumentoGeneralRequest request = DocumentoGeneralRequest.builder()
                .title(title)
                .categoria(DocumentoCategoria.valueOf(category))
                .description(description)
                .filePath(filePath)
                .version(version)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentosService.crearGeneral(request, createdBy));
    }

    @PutMapping("/generales/{id}")
    public ResponseEntity<DocumentoGeneralResponse> actualizarGeneral(
            @PathVariable Long id,
            @Valid @RequestBody DocumentoGeneralRequest request) {
        return ResponseEntity.ok(documentosService.actualizarGeneral(id, request));
    }

    @DeleteMapping("/generales/{id}")
    public ResponseEntity<Void> eliminarGeneral(@PathVariable Long id) {
        DocumentoGeneralResponse documento = documentosService.obtenerGeneral(id);
        if (documento.getFilePath() != null && !documento.getFilePath().isBlank()) {
            fileStorageService.delete(documento.getFilePath());
        }
        documentosService.eliminarGeneral(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/personales")
    public ResponseEntity<List<DocumentoPersonalResponse>> listarPersonales(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) DocumentoEstado status,
            Authentication authentication) {

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRADOR"));

        Long resolvedUserId = userId;
        if (!isAdmin && authentication != null) {
            Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
            resolvedUserId = usuario.getId();
        }

        return ResponseEntity.ok(documentosService.listarPersonales(resolvedUserId, status));
    }

    @GetMapping("/personales/{id}")
    public ResponseEntity<DocumentoPersonalResponse> obtenerPersonal(@PathVariable Long id) {
        return ResponseEntity.ok(documentosService.obtenerPersonal(id));
    }

    @PostMapping("/personales")
    public ResponseEntity<DocumentoPersonalResponse> subirDocumentoPersonal(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipo") String tipo,
            @RequestParam("issueDate") String issueDate,
            @RequestParam("expirationDate") String expirationDate,
            @RequestParam(value = "userId", required = false) Long userId,
            Authentication authentication) {

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRADOR"));

        Usuario usuario = authentication == null ? null : usuarioRepository.findByEmail(authentication.getName()).orElse(null);
        Long currentUserId = usuario != null ? usuario.getId() : userId;

        DocumentoPersonalRequest request = DocumentoPersonalRequest.builder()
                .userId(isAdmin && userId != null ? userId : currentUserId)
                .tipo(DocumentoTipo.valueOf(tipo))
                .issueDate(java.time.LocalDate.parse(issueDate))
                .expirationDate(java.time.LocalDate.parse(expirationDate))
                .filePath(fileStorageService.store(file, "documentos/personales"))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentosService.crearPersonal(request, currentUserId, isAdmin));
    }

    @PutMapping("/personales/{id}")
    public ResponseEntity<DocumentoPersonalResponse> actualizarPersonal(
            @PathVariable Long id,
            @Valid @RequestBody DocumentoPersonalRequest request) {
        return ResponseEntity.ok(documentosService.actualizarPersonal(id, request));
    }

    @PatchMapping("/personales/{id}/status")
    public ResponseEntity<DocumentoPersonalResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarDocumentoStatusRequest request,
            Authentication authentication) {

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRADOR"));

        return ResponseEntity.ok(documentosService.actualizarEstado(id, request.getStatus(), isAdmin));
    }

    @GetMapping("/solicitudes")
    public ResponseEntity<List<DocumentoSolicitudResponse>> listarSolicitudes(
            @RequestParam(required = false) Long userId,
            Authentication authentication) {

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRADOR"));

        Long resolvedUserId = userId;
        if (!isAdmin && authentication != null) {
            resolvedUserId = resolveCurrentUserId(authentication);
        }

        return ResponseEntity.ok(documentosService.listarSolicitudes(resolvedUserId, isAdmin));
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<DocumentoSolicitudResponse> crearSolicitud(
            @Valid @RequestBody DocumentoSolicitudRequest request,
            Authentication authentication) {

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRADOR"));

        String requestedBy = authentication != null ? authentication.getName() : "admin@sst.com";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentosService.crearSolicitud(request, requestedBy, isAdmin));
    }

    @PostMapping("/solicitudes/{id}/subir")
    public ResponseEntity<DocumentoPersonalResponse> subirDesdeSolicitud(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("issueDate") String issueDate,
            @RequestParam("expirationDate") String expirationDate,
            Authentication authentication) {

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRADOR"));

        Long currentUserId = isAdmin ? null : resolveCurrentUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentosService.subirDesdeSolicitud(
                        id,
                        file,
                        java.time.LocalDate.parse(issueDate),
                        java.time.LocalDate.parse(expirationDate),
                        currentUserId,
                        isAdmin));
    }

    @PatchMapping("/solicitudes/{id}/validar")
    public ResponseEntity<DocumentoSolicitudResponse> validarSolicitud(
            @PathVariable Long id,
            @RequestParam boolean aprobado,
            @RequestParam(required = false) String observacion,
            Authentication authentication) {

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRADOR"));

        DocumentoSolicitudEstado status = aprobado ? DocumentoSolicitudEstado.VALIDATED : DocumentoSolicitudEstado.REJECTED;
        return ResponseEntity.ok(documentosService.actualizarEstadoSolicitud(id, status, authentication != null ? authentication.getName() : "admin@sst.com", isAdmin));
    }

    @DeleteMapping("/personales/{id}")
    public ResponseEntity<Void> eliminarPersonal(@PathVariable Long id) {
        DocumentoPersonalResponse documento = documentosService.obtenerPersonal(id);
        if (documento.getFilePath() != null && !documento.getFilePath().isBlank()) {
            fileStorageService.delete(documento.getFilePath());
        }
        documentosService.eliminarPersonal(id);
        return ResponseEntity.noContent().build();
    }

    private String resolveFilePath(Long id, String tipo) {
        if ("general".equalsIgnoreCase(tipo)) {
            return documentoGeneralRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Documento general no encontrado."))
                    .getFilePath();
        }

        if ("personal".equalsIgnoreCase(tipo)) {
            return documentoPersonalRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Documento personal no encontrado."))
                    .getFilePath();
        }

        return documentoGeneralRepository.findById(id)
                .map(doc -> doc.getFilePath())
                .orElseGet(() -> documentoPersonalRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado."))
                        .getFilePath());
    }

    private Long resolveCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("No hay usuario autenticado.");
        }
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        return usuario.getId();
    }
}
