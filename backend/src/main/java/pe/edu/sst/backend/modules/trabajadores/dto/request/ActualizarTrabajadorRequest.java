package pe.edu.sst.backend.modules.trabajadores.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ActualizarTrabajadorRequest {

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    private String telefono;

    private String correoNotificaciones;

    @NotBlank(message = "El tipo de contrato es obligatorio")
    private String tipoContrato;

    @NotNull(message = "La sede es obligatoria")
    private Long sedeId;

    @NotNull(message = "El área es obligatoria")
    private Long areaId;

    @NotNull(message = "El cargo es obligatorio")
    private Long cargoId;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}