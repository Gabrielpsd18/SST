package pe.edu.sst.backend.modules.importaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDTO {
    private int totalRows;
    private int correctRows;
    private int errorsCount;
    private int duplicates;
    private int wouldDeactivateCount;
    private List<InvalidRowDetail> errors; 
}