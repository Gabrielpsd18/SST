package pe.edu.sst.backend.modules.trabajadores.repository;

import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {
    
    boolean existsByNumeroDocumento(String numeroDocumento);
    
    Optional<Trabajador> findByNumeroDocumento(String numeroDocumento);

    Optional<Trabajador> findByUsuarioId(Long usuarioId);

 
}