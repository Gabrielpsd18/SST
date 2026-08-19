package pe.edu.sst.backend.modules.importaciones.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_errors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long importBatchId;

    private String dni;
    private String trabajador;
    private String telefono;
    private String sede;
    private String cargo;
    @Column(length = 1000)
    private String errorMessage;

    private LocalDateTime createdAt;
}
