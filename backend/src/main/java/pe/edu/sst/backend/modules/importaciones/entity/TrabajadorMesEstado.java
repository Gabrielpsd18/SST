package pe.edu.sst.backend.modules.importaciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trabajador_mes_estado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrabajadorMesEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long trabajadorId;
    private Long importBatchId;
    private Integer month;
    private Integer year;
    private String estado;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
