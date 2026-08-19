package pe.edu.sst.backend.modules.trabajadores.repository;

import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;
import org.springframework.data.domain.Page;
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

    Page<Trabajador> findByEstadoIgnoreCase(String estado, Pageable pageable);

    Page<Trabajador> findBySedeId(Long sedeId, Pageable pageable);

    Page<Trabajador> findBySedeIdAndEstadoIgnoreCase(Long sedeId, String estado, Pageable pageable);

    @Query("select t from Trabajador t " +
            "left join fetch t.sede s " +
            "left join fetch t.cargo c " +
            "where lower(concat(t.nombreCompleto, ' ', t.numeroDocumento, ' ', coalesce(c.nombre, ''), ' ', coalesce(s.nombre, ''))) like lower(concat('%', :segment, '%')) " +
            "order by t.nombreCompleto asc")
    List<Trabajador> findBySegmento(@Param("segment") String segment, Pageable pageable);

    @Query("select t from Trabajador t " +
            "left join fetch t.sede s " +
            "left join fetch t.cargo c " +
            "where t.sede.id = :sedeId " +
            "and lower(concat(t.nombreCompleto, ' ', t.numeroDocumento, ' ', coalesce(c.nombre, ''), ' ', coalesce(s.nombre, ''))) like lower(concat('%', :segment, '%')) " +
            "order by t.nombreCompleto asc")
    List<Trabajador> findBySedeIdAndSegmento(@Param("sedeId") Long sedeId, @Param("segment") String segment, Pageable pageable);

    @Query("select t from Trabajador t " +
            "left join fetch t.sede s " +
            "left join fetch t.cargo c " +
            "where lower(t.estado) = lower(:estado) " +
            "and lower(concat(t.nombreCompleto, ' ', t.numeroDocumento, ' ', coalesce(c.nombre, ''), ' ', coalesce(s.nombre, ''))) like lower(concat('%', :segment, '%')) " +
            "order by t.nombreCompleto asc")
    List<Trabajador> findBySegmentoAndEstado(@Param("segment") String segment, @Param("estado") String estado, Pageable pageable);

    @Query("select t from Trabajador t " +
            "left join fetch t.sede s " +
            "left join fetch t.cargo c " +
            "where t.sede.id = :sedeId " +
            "and lower(t.estado) = lower(:estado) " +
            "and lower(concat(t.nombreCompleto, ' ', t.numeroDocumento, ' ', coalesce(c.nombre, ''), ' ', coalesce(s.nombre, ''))) like lower(concat('%', :segment, '%')) " +
            "order by t.nombreCompleto asc")
    List<Trabajador> findBySedeIdAndSegmentoAndEstado(@Param("sedeId") Long sedeId, @Param("segment") String segment, @Param("estado") String estado, Pageable pageable);

    long countBySedeId(Long sedeId);

    @Query("SELECT t.cargo.nombre, COUNT(t) FROM Trabajador t WHERE t.sede.id = :sedeId GROUP BY t.cargo.nombre")
    List<Object[]> countWorkersByCargoForSede(@Param("sedeId") Long sedeId);

    @Query("SELECT t.cargo.nombre, COUNT(t) FROM Trabajador t GROUP BY t.cargo.nombre")
    List<Object[]> countWorkersByCargoAll();

    @Query("SELECT t.sede.nombre, COUNT(t) FROM Trabajador t GROUP BY t.sede.nombre")
    List<Object[]> countWorkersBySedeAll();
}