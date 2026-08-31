package pe.edu.sst.backend.modules.documentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoSolicitud;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoSolicitudEstado;

import java.util.List;

public interface DocumentoSolicitudRepository extends JpaRepository<DocumentoSolicitud, Long> {
    List<DocumentoSolicitud> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<DocumentoSolicitud> findAllByOrderByCreatedAtDesc();
    List<DocumentoSolicitud> findByStatusOrderByCreatedAtDesc(DocumentoSolicitudEstado status);
}
