package pe.edu.sst.backend.modules.capacitaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.capacitaciones.entity.Capacitador;

import java.util.List;

public interface CapacitadorRepository extends JpaRepository<Capacitador, Long> {
    List<Capacitador> findByEstadoTrue();
}
