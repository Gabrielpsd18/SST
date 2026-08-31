package pe.edu.sst.backend.modules.documentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoEstado;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoTipo;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoPersonalResponse {
    private Long id;
    private Long userId;
    private Long solicitudId;
    private DocumentoTipo tipo;
    private String filePath;
    private LocalDate issueDate;
    private LocalDate expirationDate;
    private DocumentoEstado status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
