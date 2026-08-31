package pe.edu.sst.backend.modules.documentos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoTipo;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoPersonalRequest {

    @NotNull
    private Long userId;

    @NotNull
    private DocumentoTipo tipo;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private LocalDate expirationDate;

    @NotBlank
    private String filePath;
}
