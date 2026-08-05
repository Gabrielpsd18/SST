package pe.edu.sst.backend.modules.trabajadores.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.sst.backend.modules.trabajadores.entity.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByEstadoTrue();
}