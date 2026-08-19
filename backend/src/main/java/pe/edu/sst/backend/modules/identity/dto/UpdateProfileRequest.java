package pe.edu.sst.backend.modules.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Email(message = "El correo de notificaciones debe ser válido")
    private String correoNotificaciones;

    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    @Email(message = "El correo corporativo debe ser válido")
    private String email;

    @jakarta.validation.constraints.Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
