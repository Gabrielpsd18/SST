package pe.edu.sst.backend.modules.documentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoCategoria;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoGeneralResponse {
    private Long id;
    private String title;
    private DocumentoCategoria categoria;
    private String description;
    private String filePath;
    private String version;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
