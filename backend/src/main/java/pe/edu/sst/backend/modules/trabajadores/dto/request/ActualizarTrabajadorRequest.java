package pe.edu.sst.backend.modules.trabajadores.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ActualizarTrabajadorRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 200, message = "El nombre completo no puede superar 200 caracteres")
    private String nombreCompleto;

    private String telefono;

    private String correoNotificaciones;

    @NotNull(message = "La sede es obligatoria")
    private Long sedeId;

    @NotNull(message = "El cargo es obligatorio")
    private Long cargoId;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(ACTIVO|CESADO)$", message = "El estado debe ser ACTIVO o CESADO")
    private String estado;
}
