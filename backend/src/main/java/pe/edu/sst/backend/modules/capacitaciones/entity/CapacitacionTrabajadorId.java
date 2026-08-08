package pe.edu.sst.backend.modules.capacitaciones.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CapacitacionTrabajadorId implements Serializable {

    private Long capacitacionId;
    private Long trabajadorId;
}
