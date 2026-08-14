package pe.edu.sst.backend.modules.importaciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long importBatchId;
    private Long trabajadorId;
    private String action;

    @Column(columnDefinition = "TEXT")
    private String beforeJson;
    @Column(columnDefinition = "TEXT")
    private String afterJson;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private Long performedBy;
}
