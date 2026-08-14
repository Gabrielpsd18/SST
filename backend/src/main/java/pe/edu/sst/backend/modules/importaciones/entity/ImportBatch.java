package pe.edu.sst.backend.modules.importaciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_batch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private Integer month;
    private Integer year;
    private Long importerId;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String status;
    private Integer summaryCreated;
    private Integer summaryReactivated;
    private Integer summaryDeactivated;
    private Integer summaryUpdated;
    private Integer summaryErrors;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
