package pe.edu.sst.backend.modules.trabajadores.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class TrabajadorResponse {

    private Long id;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombreCompleto;
    private String telefono;
    private String correoNotificaciones;
    private String tipoContrato;
    private String estado;

    private Long sedeId;
    private String sedeNombre;

    private Long cargoId;
    private String cargoNombre;

    private Long usuarioId;
    private LocalDateTime createdAt;
}
