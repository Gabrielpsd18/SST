package pe.edu.sst.backend.modules.importaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvalidRowDetail {
    private String dni;
    private String trabajador;
    private String telefono;
    private String sede;
    private String cargo;
    private String errorMessage; // Motivo por el cual falló (ej. "DNI inválido", "Sede no encontrada")
}