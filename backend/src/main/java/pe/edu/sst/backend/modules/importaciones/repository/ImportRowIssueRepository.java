package pe.edu.sst.backend.modules.importaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.importaciones.entity.ImportRowIssue;
import java.util.List;

public interface ImportRowIssueRepository extends JpaRepository<ImportRowIssue, Long> {
    List<ImportRowIssue> findByImportBatchId(Long batchId);
}
