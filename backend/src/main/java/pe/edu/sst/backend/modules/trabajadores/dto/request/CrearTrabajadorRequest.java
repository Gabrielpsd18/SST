package pe.edu.sst.backend.modules.trabajadores.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CrearTrabajadorRequest {

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe contener exactamente 8 dígitos")
    private String numeroDocumento;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 200, message = "El nombre completo no puede superar 200 caracteres")
    private String nombreCompleto;

    private String telefono;

    private String correoNotificaciones;

    @NotNull(message = "La sede es obligatoria")
    private Long sedeId;

    @NotNull(message = "El cargo es obligatorio")
    private Long cargoId;
}
