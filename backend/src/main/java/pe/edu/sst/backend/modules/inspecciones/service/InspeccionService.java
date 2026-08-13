package pe.edu.sst.backend.modules.inspecciones.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.sst.backend.modules.inspecciones.dto.request.CrearInspeccionRequest;
import pe.edu.sst.backend.modules.inspecciones.dto.response.InspeccionResponse;

public interface InspeccionService {
    InspeccionResponse crear(CrearInspeccionRequest request, String userEmail);
    InspeccionResponse actualizar(Long id, CrearInspeccionRequest request);
    Page<InspeccionResponse> listarPaginado(Pageable pageable);
    InspeccionResponse obtenerPorId(Long id);
    void cambiarEstado(Long id, String estado);
}
