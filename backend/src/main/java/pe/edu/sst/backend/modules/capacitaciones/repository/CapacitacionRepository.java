package pe.edu.sst.backend.modules.capacitaciones.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sst.backend.modules.capacitaciones.entity.Capacitacion;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CapacitacionRepository extends JpaRepository<Capacitacion, Long> {
    Page<Capacitacion> findAllByOrderByFechaProgramadaDesc(Pageable pageable);

    @Query("SELECT DISTINCT c FROM Capacitacion c " +
           "JOIN c.trabajadoresAsignados ct " +
           "WHERE ct.trabajador.sede.id = :sedeId " +
           "ORDER BY c.fechaProgramada DESC")
    Page<Capacitacion> findBySedeIdOrderByFechaProgramadaDesc(@Param("sedeId") Long sedeId, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT c) FROM Capacitacion c JOIN c.trabajadoresAsignados ct WHERE ct.trabajador.sede.id = :sedeId")
    long countCapacitacionesBySedeId(@Param("sedeId") Long sedeId);

    @Query("SELECT c.estado, COUNT(DISTINCT c) FROM Capacitacion c JOIN c.trabajadoresAsignados ct WHERE ct.trabajador.sede.id = :sedeId GROUP BY c.estado")
    java.util.List<Object[]> countCapacitacionesByStateForSede(@Param("sedeId") Long sedeId);

    @Query("SELECT c.estado, COUNT(c) FROM Capacitacion c GROUP BY c.estado")
    java.util.List<Object[]> countCapacitacionesByStateAll();

    @Query("SELECT c.tipo, COUNT(DISTINCT c) FROM Capacitacion c JOIN c.trabajadoresAsignados ct WHERE ct.trabajador.sede.id = :sedeId GROUP BY c.tipo")
    java.util.List<Object[]> countCapacitacionesByTypeForSede(@Param("sedeId") Long sedeId);

    @Query("SELECT c.tipo, COUNT(c) FROM Capacitacion c GROUP BY c.tipo")
    java.util.List<Object[]> countCapacitacionesByTypeAll();

    java.util.List<Capacitacion> findTop5ByOrderByIdDesc();

    @Query("SELECT DISTINCT c FROM Capacitacion c JOIN c.trabajadoresAsignados ct WHERE ct.trabajador.sede.id = :sedeId ORDER BY c.id DESC")
    java.util.List<Capacitacion> findTop5BySedeIdOrderByIdDesc(@Param("sedeId") Long sedeId, Pageable pageable);

    java.util.List<Capacitacion> findTop5ByFechaProgramadaAfterOrderByFechaProgramadaAsc(java.time.LocalDateTime now);

    @Query("SELECT DISTINCT c FROM Capacitacion c JOIN c.trabajadoresAsignados ct WHERE ct.trabajador.sede.id = :sedeId AND c.fechaProgramada >= :now ORDER BY c.fechaProgramada ASC")
    java.util.List<Capacitacion> findTop5BySedeIdAndFechaProgramadaAfterOrderByFechaProgramadaAsc(@Param("sedeId") Long sedeId, @Param("now") java.time.LocalDateTime now, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Capacitacion c " +
           "LEFT JOIN c.trabajadoresAsignados ct " +
           "WHERE (:trabajadorId IS NULL OR ct.trabajador.id = :trabajadorId) " +
           "AND (:filtro = 'TODOS' " +
           "     OR (:filtro = 'PENDIENTES' AND (c.estado IN ('PROGRAMADO', 'EN_CURSO') OR c.fechaProgramada >= :now)) " +
           "     OR (:filtro = 'REALIZADAS' AND (c.estado = 'COMPLETADO' OR c.fechaProgramada < :now))) " +
           "ORDER BY c.fechaProgramada DESC")
    Page<Capacitacion> findForMobileWithFilter(
            @Param("trabajadorId") Long trabajadorId,
            @Param("filtro") String filtro,
            @Param("now") java.time.LocalDateTime now,
            Pageable pageable);
}
