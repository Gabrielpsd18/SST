package pe.edu.sst.backend.modules.importaciones.service;

import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.modules.importaciones.dto.ImportPreviewResult;
import pe.edu.sst.backend.modules.importaciones.entity.ImportRowIssue;
import java.util.List;

public interface ImportService {
    ImportPreviewResult previewImport(MultipartFile file, String monthOption) throws Exception;
    ImportPreviewResult applyImport(Long batchId) throws Exception;

    List<ImportRowIssue> listIssues(Long batchId);
    void resolveIssue(Long batchId, Long issueId, String action);
}
