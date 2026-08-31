package pe.edu.sst.backend.modules.documentos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoTipo;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoSolicitudRequest {

    @NotNull
    private Long userId;

    @NotNull
    private DocumentoTipo tipo;

    @Size(max = 1000)
    private String message;
}
