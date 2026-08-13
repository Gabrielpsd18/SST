package pe.edu.sst.backend.modules.inspecciones.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "inspecciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inspeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String tema;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "fecha_inspeccion", nullable = false)
    private LocalDate fechaInspeccion;

    @Column(name = "hora_inspeccion", nullable = false)
    private LocalTime horaInspeccion;

    @Column(length = 500)
    private String observaciones;

    @Builder.Default
    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "inspecciones_responsables",
            joinColumns = @JoinColumn(name = "inspeccion_id"),
            inverseJoinColumns = @JoinColumn(name = "trabajador_id")
    )
    private Set<Trabajador> responsables = new HashSet<>();
}
