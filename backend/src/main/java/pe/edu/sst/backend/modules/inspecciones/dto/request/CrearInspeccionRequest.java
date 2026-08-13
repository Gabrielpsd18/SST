package pe.edu.sst.backend.modules.inspecciones.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearInspeccionRequest {

    @NotBlank(message = "El tema de la inspección es obligatorio")
    private String tema;

    @NotBlank(message = "El tipo de inspección es obligatorio")
    private String tipo;

    @NotNull(message = "La fecha de inspección es obligatoria")
    private LocalDate fechaInspeccion;

    @NotNull(message = "La hora de inspección es obligatoria")
    private LocalTime horaInspeccion;

    @NotEmpty(message = "Debe seleccionar al menos un responsable")
    private List<Long> responsableIds;

    private String observaciones;
}
