package pe.edu.sst.backend.modules.documentos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoCategoria;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoGeneralRequest {

    @NotBlank
    private String title;

    @NotNull
    private DocumentoCategoria categoria;

    private String description;

    @NotBlank
    private String filePath;

    @Builder.Default
    private String version = "1.0";
}
