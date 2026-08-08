package pe.edu.sst.backend.modules.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String correoCorporativo;
    private String sede;
    private String area;
    private String cargo;
    private String correoNotificaciones;
    private String telefono;
    private Integer capacitacionesCompletadas;
    private Integer capacitacionesPendientes;
}
