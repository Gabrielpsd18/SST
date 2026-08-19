package pe.edu.sst.backend.modules.inspecciones.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.inspecciones.entity.Inspeccion;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InspeccionRepository extends JpaRepository<Inspeccion, Long> {
    Page<Inspeccion> findAllByOrderByFechaInspeccionDescHoraInspeccionDesc(Pageable pageable);

    @Query("SELECT DISTINCT i FROM Inspeccion i " +
           "JOIN i.responsables r " +
           "WHERE r.sede.id = :sedeId " +
           "ORDER BY i.fechaInspeccion DESC, i.horaInspeccion DESC")
    Page<Inspeccion> findBySedeIdOrderByFechaInspeccionDescHoraInspeccionDesc(@Param("sedeId") Long sedeId, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT i) FROM Inspeccion i JOIN i.responsables r WHERE r.sede.id = :sedeId")
    long countInspeccionesBySedeId(@Param("sedeId") Long sedeId);

    @Query("SELECT i.estado, COUNT(DISTINCT i) FROM Inspeccion i JOIN i.responsables r WHERE r.sede.id = :sedeId GROUP BY i.estado")
    java.util.List<Object[]> countInspeccionesByStateForSede(@Param("sedeId") Long sedeId);

    @Query("SELECT i.estado, COUNT(i) FROM Inspeccion i GROUP BY i.estado")
    java.util.List<Object[]> countInspeccionesByStateAll();

    @Query("SELECT i.tipo, COUNT(DISTINCT i) FROM Inspeccion i JOIN i.responsables r WHERE r.sede.id = :sedeId GROUP BY i.tipo")
    java.util.List<Object[]> countInspeccionesByTypeForSede(@Param("sedeId") Long sedeId);

    @Query("SELECT i.tipo, COUNT(i) FROM Inspeccion i GROUP BY i.tipo")
    java.util.List<Object[]> countInspeccionesByTypeAll();

    java.util.List<Inspeccion> findTop5ByOrderByIdDesc();

    @Query("SELECT DISTINCT i FROM Inspeccion i JOIN i.responsables r WHERE r.sede.id = :sedeId ORDER BY i.id DESC")
    java.util.List<Inspeccion> findTop5BySedeIdOrderByIdDesc(@Param("sedeId") Long sedeId, Pageable pageable);

    java.util.List<Inspeccion> findTop5ByFechaInspeccionAfterOrderByFechaInspeccionAsc(java.time.LocalDate now);

    @Query("SELECT DISTINCT i FROM Inspeccion i JOIN i.responsables r WHERE r.sede.id = :sedeId AND i.fechaInspeccion >= :now ORDER BY i.fechaInspeccion ASC, i.horaInspeccion ASC")
    java.util.List<Inspeccion> findTop5BySedeIdAndFechaInspeccionAfterOrderByFechaInspeccionAscHoraInspeccionAsc(@Param("sedeId") Long sedeId, @Param("now") java.time.LocalDate now, Pageable pageable);
}
