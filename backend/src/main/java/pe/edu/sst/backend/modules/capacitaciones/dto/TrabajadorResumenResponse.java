package pe.edu.sst.backend.modules.capacitaciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrabajadorResumenResponse {
    private Long id;
    private String nombreCompleto;
    private String numeroDocumento;
    private String sedeNombre;
    private String cargoNombre;
}
