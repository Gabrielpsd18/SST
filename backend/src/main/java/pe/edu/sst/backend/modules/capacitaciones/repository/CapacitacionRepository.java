package pe.edu.sst.backend.modules.capacitaciones.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.capacitaciones.entity.Capacitacion;

public interface CapacitacionRepository extends JpaRepository<Capacitacion, Long> {
    Page<Capacitacion> findAllByOrderByFechaProgramadaDesc(Pageable pageable);
}
