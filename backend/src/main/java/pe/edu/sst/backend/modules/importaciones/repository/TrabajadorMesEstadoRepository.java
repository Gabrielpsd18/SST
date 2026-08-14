package pe.edu.sst.backend.modules.importaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.importaciones.entity.TrabajadorMesEstado;

import java.util.List;

public interface TrabajadorMesEstadoRepository extends JpaRepository<TrabajadorMesEstado, Long> {
    List<TrabajadorMesEstado> findByTrabajadorId(Long trabajadorId);
}
