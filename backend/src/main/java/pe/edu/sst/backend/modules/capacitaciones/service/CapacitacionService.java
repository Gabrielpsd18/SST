package pe.edu.sst.backend.modules.capacitaciones.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.sst.backend.modules.capacitaciones.dto.CrearCapacitacionRequest;
import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitacionResponse;

public interface CapacitacionService {
    CapacitacionResponse programar(CrearCapacitacionRequest request, String userEmail);
    Page<CapacitacionResponse> listarPaginado(Pageable pageable);
    CapacitacionResponse obtenerPorId(Long id);
    CapacitacionResponse actualizar(Long id, CrearCapacitacionRequest request);
}
