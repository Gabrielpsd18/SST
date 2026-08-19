package pe.edu.sst.backend.modules.importaciones.service;

import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.modules.importaciones.dto.ImportResultDTO;
import pe.edu.sst.backend.modules.importaciones.dto.InvalidRowDetail;
import pe.edu.sst.backend.modules.importaciones.entity.ImportError;
import java.util.List;

public interface ImportService {
    ImportResultDTO processAndImplementImport(MultipartFile file, String monthOption) throws Exception;

    void processSingleRow(InvalidRowDetail rowDetail) throws Exception;

    // Lista de errores pendientes para mostrar en la página de importaciones
    List<ImportError> getPendingErrors();

    // Eliminar un error pendiente (cuando el usuario decide descartar)
    void deletePendingError(Long id);

    // Reintentar la fila de error por id (si se procesa correctamente, se eliminará)
    void retryPendingError(Long id) throws Exception;
}