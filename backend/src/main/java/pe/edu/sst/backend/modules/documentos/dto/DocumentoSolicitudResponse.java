package pe.edu.sst.backend.modules.documentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoSolicitudEstado;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoTipo;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoSolicitudResponse {
    private Long id;
    private Long userId;
    private DocumentoTipo tipo;
    private String message;
    private DocumentoSolicitudEstado status;
    private String requestedBy;
    private String validatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime validatedAt;
}
