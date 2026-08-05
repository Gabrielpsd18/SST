package pe.edu.sst.backend.modules.identity.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.identity.entity.Rol;
import pe.edu.sst.backend.modules.identity.enums.RoleName;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(RoleName nombre);

}