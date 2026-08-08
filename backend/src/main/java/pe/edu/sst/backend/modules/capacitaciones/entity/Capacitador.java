package pe.edu.sst.backend.modules.capacitaciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "capacitadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Capacitador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(length = 100)
    private String apellidos;

    @Column(nullable = false, length = 100)
    private String empresa;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 128)
    private String correo;

    @Column(length = 100)
    private String especialidad;

    @Builder.Default
    @Column(nullable = false)
    private Boolean estado = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
