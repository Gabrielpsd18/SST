package pe.edu.sst.backend.modules.importaciones.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportPreviewResult {
    private Long batchId;
    private int totalRows;
    private int duplicates;
    private int invalidRows;
    private int missingSede;
    private int missingCargo;
    private int newCount;
    private int reactivatedCount;
    private int wouldDeactivateCount;
    private int errors;
    private List<Map<String, String>> sampleRows;
}
