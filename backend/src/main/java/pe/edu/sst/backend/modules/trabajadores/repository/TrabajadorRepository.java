package pe.edu.sst.backend.modules.trabajadores.repository;

import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {

    boolean existsByNumeroDocumento(String numeroDocumento);

    Optional<Trabajador> findByNumeroDocumento(String numeroDocumento);

    Optional<Trabajador> findByUsuarioId(Long usuarioId);

    @Query("select t from Trabajador t " +
            "left join fetch t.sede s " +
            "left join fetch t.cargo c " +
            "where lower(concat(t.nombreCompleto, ' ', t.numeroDocumento, ' ', coalesce(c.nombre, ''), ' ', coalesce(s.nombre, ''))) like lower(concat('%', :segment, '%')) "
            +
            "order by t.nombreCompleto asc")
    List<Trabajador> findBySegmento(@Param("segment") String segment, Pageable pageable);
}