package pe.edu.sst.backend.modules.capacitaciones.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacitadorRequest {

    @NotBlank(message = "El nombre del capacitador es obligatorio")
    private String nombres;

    private String apellidos;

    @NotBlank(message = "La empresa u organización es obligatoria")
    private String empresa;

    @NotBlank(message = "El teléfono de contacto es obligatorio")
    private String telefono;

    private String correo;
    private String especialidad;
}
