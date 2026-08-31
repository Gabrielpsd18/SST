package pe.edu.sst.backend.modules.documentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoGeneral;

import java.util.List;

public interface DocumentoGeneralRepository extends JpaRepository<DocumentoGeneral, Long> {
    List<DocumentoGeneral> findAllByOrderByCreatedAtDesc();
}
