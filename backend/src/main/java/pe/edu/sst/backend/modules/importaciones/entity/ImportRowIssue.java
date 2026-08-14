package pe.edu.sst.backend.modules.importaciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_row_issue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportRowIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long importBatchId;
    private Integer rowNumber;

    @Column(columnDefinition = "TEXT")
    private String rawRowJson;

    private String issueType;
    @Column(columnDefinition = "TEXT")
    private String issueDetails;

    private Boolean resolved;
    private Long resolvedBy;
    private LocalDateTime resolvedAt;
    private String actionTaken;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
