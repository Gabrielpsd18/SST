package pe.edu.sst.backend.modules.trabajadores.service;

import pe.edu.sst.backend.modules.trabajadores.dto.request.ActualizarTrabajadorRequest;
import pe.edu.sst.backend.modules.trabajadores.dto.request.CrearTrabajadorRequest;
import pe.edu.sst.backend.modules.trabajadores.dto.response.MaestraResponse;
import pe.edu.sst.backend.modules.trabajadores.dto.response.TrabajadorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TrabajadorService {
    TrabajadorResponse crear(CrearTrabajadorRequest request);
    TrabajadorResponse actualizar(Long id, ActualizarTrabajadorRequest request);
    TrabajadorResponse obtenerPorId(Long id);
    Page<TrabajadorResponse> listarPaginado(Pageable pageable);
    List<TrabajadorResponse> buscarPorSegmento(String segment, int limit);
    void cambiarEstado(Long id, String nuevoEstado);

    List<MaestraResponse> listarSedes();
    List<MaestraResponse> listarCargos();
}
