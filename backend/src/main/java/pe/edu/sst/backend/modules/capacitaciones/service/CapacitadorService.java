package pe.edu.sst.backend.modules.capacitaciones.service;

import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitadorRequest;
import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitadorResponse;

import java.util.List;

public interface CapacitadorService {
    CapacitadorResponse crear(CapacitadorRequest request);
    CapacitadorResponse actualizar(Long id, CapacitadorRequest request);
    List<CapacitadorResponse> listarActivos();
    CapacitadorResponse obtenerPorId(Long id);
}
