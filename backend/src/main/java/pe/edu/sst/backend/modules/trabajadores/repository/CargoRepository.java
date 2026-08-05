package pe.edu.sst.backend.modules.trabajadores.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.sst.backend.modules.trabajadores.entity.Cargo;

public interface CargoRepository extends JpaRepository<Cargo, Long> {
    List<Cargo> findByEstadoTrue();
}