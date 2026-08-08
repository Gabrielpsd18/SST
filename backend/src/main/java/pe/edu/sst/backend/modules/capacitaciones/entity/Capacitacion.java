package pe.edu.sst.backend.modules.capacitaciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "capacitaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Capacitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String tema;

    @Column(nullable = false, length = 50)
    private String tipo; // CHARLA_5_MINUTOS, INDUCCION, CAPACITACION

    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(name = "duracion_horas", nullable = false, precision = 4, scale = 2)
    private BigDecimal duracionHoras;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capacitador_id", nullable = false)
    private Capacitador capacitador;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Builder.Default
    @Column(nullable = false, length = 30)
    private String estado = "PROGRAMADO"; // PROGRAMADO, EN_CURSO, COMPLETADO, CANCELADO

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "capacitacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CapacitacionTrabajador> trabajadoresAsignados = new HashSet<>();
}
