package pe.edu.sst.backend.modules.trabajadores.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.sst.backend.modules.trabajadores.entity.Sede;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    List<Sede> findByEstadoTrue();
}
