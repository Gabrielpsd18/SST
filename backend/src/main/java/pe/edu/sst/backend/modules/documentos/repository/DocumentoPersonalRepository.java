package pe.edu.sst.backend.modules.documentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoEstado;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoPersonal;

import java.util.List;

public interface DocumentoPersonalRepository extends JpaRepository<DocumentoPersonal, Long> {
    List<DocumentoPersonal> findAllByOrderByCreatedAtDesc();
    List<DocumentoPersonal> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<DocumentoPersonal> findByStatusOrderByCreatedAtDesc(DocumentoEstado status);
    List<DocumentoPersonal> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, DocumentoEstado status);
}
