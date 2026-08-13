package pe.edu.sst.backend.modules.inspecciones.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.inspecciones.entity.Inspeccion;

public interface InspeccionRepository extends JpaRepository<Inspeccion, Long> {
    Page<Inspeccion> findAllByOrderByFechaInspeccionDescHoraInspeccionDesc(Pageable pageable);
}
