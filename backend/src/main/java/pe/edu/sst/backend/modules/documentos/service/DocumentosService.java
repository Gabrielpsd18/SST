package pe.edu.sst.backend.modules.documentos.service;

import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.modules.documentos.dto.*;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoEstado;
import pe.edu.sst.backend.modules.documentos.entity.DocumentoSolicitudEstado;

import java.time.LocalDate;
import java.util.List;

public interface DocumentosService {
    List<DocumentoGeneralResponse> listarGenerales();
    DocumentoGeneralResponse obtenerGeneral(Long id);
    DocumentoGeneralResponse crearGeneral(DocumentoGeneralRequest request, String createdBy);
    DocumentoGeneralResponse actualizarGeneral(Long id, DocumentoGeneralRequest request);
    void eliminarGeneral(Long id);

    List<DocumentoPersonalResponse> listarPersonales(Long userId, DocumentoEstado status);
    DocumentoPersonalResponse obtenerPersonal(Long id);
    DocumentoPersonalResponse crearPersonal(DocumentoPersonalRequest request, Long currentUserId, boolean isAdmin);
    DocumentoPersonalResponse actualizarPersonal(Long id, DocumentoPersonalRequest request);
    DocumentoPersonalResponse actualizarEstado(Long id, DocumentoEstado status, boolean isAdmin);
    DocumentoPersonalResponse subirDesdeSolicitud(Long solicitudId, MultipartFile file, LocalDate issueDate, LocalDate expirationDate, Long currentUserId, boolean isAdmin);
    void eliminarPersonal(Long id);

    List<DocumentoSolicitudResponse> listarSolicitudes(Long userId, boolean isAdmin);
    DocumentoSolicitudResponse obtenerSolicitud(Long id);
    DocumentoSolicitudResponse crearSolicitud(DocumentoSolicitudRequest request, String requestedBy, boolean isAdmin);
    DocumentoSolicitudResponse actualizarEstadoSolicitud(Long id, DocumentoSolicitudEstado status, String validatedBy, boolean isAdmin);
}
