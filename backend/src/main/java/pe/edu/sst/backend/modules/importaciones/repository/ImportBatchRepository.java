package pe.edu.sst.backend.modules.importaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.importaciones.entity.ImportBatch;

public interface ImportBatchRepository extends JpaRepository<ImportBatch, Long> {
}
