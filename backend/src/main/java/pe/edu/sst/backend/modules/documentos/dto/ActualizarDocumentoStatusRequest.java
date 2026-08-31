package pe.edu.sst.backend.modules.documentos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoEstado;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarDocumentoStatusRequest {

    @NotNull
    private DocumentoEstado status;
}
