package pe.edu.sst.backend.modules.importaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.importaciones.entity.ImportError;
import java.util.List;

public interface ImportErrorRepository extends JpaRepository<ImportError, Long> {
    List<ImportError> findByOrderByCreatedAtDesc();
    List<ImportError> findByImportBatchId(Long importBatchId);
    List<ImportError> findByDni(String dni);
    boolean existsByImportBatchIdAndDniAndErrorMessage(Long importBatchId, String dni, String errorMessage);
}
