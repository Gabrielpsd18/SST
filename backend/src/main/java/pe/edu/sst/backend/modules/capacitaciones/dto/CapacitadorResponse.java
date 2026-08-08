package pe.edu.sst.backend.modules.capacitaciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacitadorResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String empresa;
    private String telefono;
    private String correo;
    private String especialidad;
    private Boolean estado;
}
