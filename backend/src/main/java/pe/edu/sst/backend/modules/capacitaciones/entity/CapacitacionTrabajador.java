package pe.edu.sst.backend.modules.capacitaciones.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;

@Entity
@Table(name = "capacitacion_trabajadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacitacionTrabajador {

    @EmbeddedId
    private CapacitacionTrabajadorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("capacitacionId")
    @JoinColumn(name = "capacitacion_id")
    private Capacitacion capacitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("trabajadorId")
    @JoinColumn(name = "trabajador_id")
    private Trabajador trabajador;

    @Builder.Default
    @Column(length = 20)
    private String asistencia = "PENDIENTE";
}
