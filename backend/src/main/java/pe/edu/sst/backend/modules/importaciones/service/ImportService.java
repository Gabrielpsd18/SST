package pe.edu.sst.backend.modules.importaciones.service;

import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.modules.importaciones.dto.ImportPreviewResult;

import java.util.List;

public interface ImportService {
    ImportPreviewResult previewImport(MultipartFile file, String monthOption, boolean autoApply, boolean createMissing) throws Exception;
    ImportPreviewResult applyImport(Long batchId) throws Exception;

    List<pe.edu.sst.backend.modules.importaciones.entity.ImportRowIssue> listIssues(Long batchId);
    void resolveIssue(Long batchId, Long issueId, String action);
}
