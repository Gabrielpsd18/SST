package pe.edu.sst.backend.modules.documentos.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.modules.documentos.dto.*;
import pe.edu.sst.backend.modules.documentos.entity.*;
import pe.edu.sst.backend.modules.documentos.repository.*;
import pe.edu.sst.backend.modules.documentos.service.DocumentosService;
import pe.edu.sst.backend.shared.storage.FileStorageService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentosServiceImpl implements DocumentosService {

    private final DocumentoGeneralRepository documentoGeneralRepository;
    private final DocumentoPersonalRepository documentoPersonalRepository;
    private final DocumentoSolicitudRepository documentoSolicitudRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoGeneralResponse> listarGenerales() {
        return documentoGeneralRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toGeneralResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoGeneralResponse obtenerGeneral(Long id) {
        DocumentoGeneral entity = documentoGeneralRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento general no encontrado."));
        return toGeneralResponse(entity);
    }

    @Override
    @Transactional
    public DocumentoGeneralResponse crearGeneral(DocumentoGeneralRequest request, String createdBy) {
        DocumentoGeneral entity = DocumentoGeneral.builder()
                .title(request.getTitle())
                .categoria(request.getCategoria())
                .description(request.getDescription())
                .filePath(request.getFilePath())
                .version(request.getVersion())
                .createdBy(createdBy)
                .build();

        return toGeneralResponse(documentoGeneralRepository.save(entity));
    }

    @Override
    @Transactional
    public DocumentoGeneralResponse actualizarGeneral(Long id, DocumentoGeneralRequest request) {
        DocumentoGeneral entity = documentoGeneralRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento general no encontrado."));

        entity.setTitle(request.getTitle());
        entity.setCategoria(request.getCategoria());
        entity.setDescription(request.getDescription());
        entity.setFilePath(request.getFilePath());
        entity.setVersion(request.getVersion());

        return toGeneralResponse(documentoGeneralRepository.save(entity));
    }

    @Override
    @Transactional
    public void eliminarGeneral(Long id) {
        DocumentoGeneral entity = documentoGeneralRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento general no encontrado."));
        documentoGeneralRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoPersonalResponse> listarPersonales(Long userId, DocumentoEstado status) {
        if (userId != null && status != null) {
            return documentoPersonalRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status)
                    .stream().map(this::toPersonalResponse).toList();
        }

        if (userId != null) {
            return documentoPersonalRepository.findByUserIdOrderByCreatedAtDesc(userId)
                    .stream().map(this::toPersonalResponse).toList();
        }

        if (status != null) {
            return documentoPersonalRepository.findByStatusOrderByCreatedAtDesc(status)
                    .stream().map(this::toPersonalResponse).toList();
        }

        return documentoPersonalRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toPersonalResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoPersonalResponse obtenerPersonal(Long id) {
        DocumentoPersonal entity = documentoPersonalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento personal no encontrado."));
        return toPersonalResponse(entity);
    }

    @Override
    @Transactional
    public DocumentoPersonalResponse crearPersonal(DocumentoPersonalRequest request, Long currentUserId, boolean isAdmin) {
        Long targetUserId = isAdmin ? request.getUserId() : currentUserId;

        DocumentoPersonal entity = DocumentoPersonal.builder()
                .userId(targetUserId)
                .tipo(request.getTipo())
                .filePath(request.getFilePath())
                .issueDate(request.getIssueDate())
                .expirationDate(request.getExpirationDate())
                .status(DocumentoEstado.PENDING)
                .build();

        return toPersonalResponse(documentoPersonalRepository.save(entity));
    }

    @Override
    @Transactional
    public DocumentoPersonalResponse actualizarPersonal(Long id, DocumentoPersonalRequest request) {
        DocumentoPersonal entity = documentoPersonalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento personal no encontrado."));

        entity.setUserId(request.getUserId());
        entity.setTipo(request.getTipo());
        entity.setFilePath(request.getFilePath());
        entity.setIssueDate(request.getIssueDate());
        entity.setExpirationDate(request.getExpirationDate());

        return toPersonalResponse(documentoPersonalRepository.save(entity));
    }

    @Override
    @Transactional
    public DocumentoPersonalResponse actualizarEstado(Long id, DocumentoEstado status, boolean isAdmin) {
        if (!isAdmin) {
            throw new SecurityException("Solo un administrador puede aprobar o rechazar documentos personales.");
        }

        DocumentoPersonal documento = documentoPersonalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado."));

        documento.setStatus(status);
        return toPersonalResponse(documentoPersonalRepository.save(documento));
    }

    @Override
    @Transactional
    public DocumentoPersonalResponse subirDesdeSolicitud(Long solicitudId, MultipartFile file, LocalDate issueDate, LocalDate expirationDate, Long currentUserId, boolean isAdmin) {
        DocumentoSolicitud solicitud = documentoSolicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada."));

        if (!isAdmin && !solicitud.getUserId().equals(currentUserId)) {
            throw new SecurityException("No puedes subir un documento para otra persona.");
        }

        if (solicitud.getStatus() == DocumentoSolicitudEstado.REJECTED || solicitud.getStatus() == DocumentoSolicitudEstado.VALIDATED) {
            throw new IllegalStateException("La solicitud ya fue procesada y no puede recibir más archivos.");
        }

        String filePath = fileStorageService.store(file, "documentos/personales");

        DocumentoPersonal entity = DocumentoPersonal.builder()
                .userId(solicitud.getUserId())
                .solicitudId(solicitud.getId())
                .tipo(solicitud.getTipo())
                .filePath(filePath)
                .issueDate(issueDate)
                .expirationDate(expirationDate)
                .status(DocumentoEstado.APPROVED)
                .build();

        DocumentoPersonal saved = documentoPersonalRepository.save(entity);

        solicitud.setStatus(DocumentoSolicitudEstado.COMPLETED);
        solicitud.setCompletedAt(LocalDateTime.now());
        documentoSolicitudRepository.save(solicitud);

        return toPersonalResponse(saved);
    }

    @Override
    @Transactional
    public void eliminarPersonal(Long id) {
        DocumentoPersonal entity = documentoPersonalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento personal no encontrado."));
        documentoPersonalRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoSolicitudResponse> listarSolicitudes(Long userId, boolean isAdmin) {
        List<DocumentoSolicitud> solicitudes;
        if (isAdmin) {
            solicitudes = documentoSolicitudRepository.findAllByOrderByCreatedAtDesc();
        } else if (userId != null) {
            solicitudes = documentoSolicitudRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else {
            solicitudes = List.of();
        }

        return solicitudes.stream().map(this::toSolicitudResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoSolicitudResponse obtenerSolicitud(Long id) {
        DocumentoSolicitud entity = documentoSolicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada."));
        return toSolicitudResponse(entity);
    }

    @Override
    @Transactional
    public DocumentoSolicitudResponse crearSolicitud(DocumentoSolicitudRequest request, String requestedBy, boolean isAdmin) {
        if (!isAdmin) {
            throw new SecurityException("Solo un administrador puede solicitar documentos para un trabajador.");
        }

        DocumentoSolicitud entity = DocumentoSolicitud.builder()
                .userId(request.getUserId())
                .tipo(request.getTipo())
                .message(request.getMessage())
                .status(DocumentoSolicitudEstado.PENDING)
                .requestedBy(requestedBy)
                .build();

        return toSolicitudResponse(documentoSolicitudRepository.save(entity));
    }

    @Override
    @Transactional
    public DocumentoSolicitudResponse actualizarEstadoSolicitud(Long id, DocumentoSolicitudEstado status, String validatedBy, boolean isAdmin) {
        if (!isAdmin) {
            throw new SecurityException("Solo un administrador puede validar una solicitud.");
        }

        DocumentoSolicitud solicitud = documentoSolicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada."));

        solicitud.setStatus(status);
        solicitud.setValidatedBy(validatedBy);
        if (status == DocumentoSolicitudEstado.VALIDATED) {
            solicitud.setValidatedAt(LocalDateTime.now());
        }
        return toSolicitudResponse(documentoSolicitudRepository.save(solicitud));
    }

    private DocumentoGeneralResponse toGeneralResponse(DocumentoGeneral entity) {
        return DocumentoGeneralResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .categoria(entity.getCategoria())
                .description(entity.getDescription())
                .filePath(entity.getFilePath())
                .version(entity.getVersion())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private DocumentoPersonalResponse toPersonalResponse(DocumentoPersonal entity) {
        return DocumentoPersonalResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .solicitudId(entity.getSolicitudId())
                .tipo(entity.getTipo())
                .filePath(entity.getFilePath())
                .issueDate(entity.getIssueDate())
                .expirationDate(entity.getExpirationDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private DocumentoSolicitudResponse toSolicitudResponse(DocumentoSolicitud entity) {
        return DocumentoSolicitudResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .tipo(entity.getTipo())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .requestedBy(entity.getRequestedBy())
                .validatedBy(entity.getValidatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .completedAt(entity.getCompletedAt())
                .validatedAt(entity.getValidatedAt())
                .build();
    }
}
