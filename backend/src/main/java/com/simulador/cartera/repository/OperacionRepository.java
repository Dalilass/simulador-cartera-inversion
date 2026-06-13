package com.simulador.cartera.repository;

import com.simulador.cartera.entity.Operacion;
import com.simulador.cartera.enums.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperacionRepository extends JpaRepository<Operacion, Long> {

    List<Operacion> findByActivoId(Long activoId);

    List<Operacion> findByTipoOperacion(TipoOperacion tipoOperacion);

    List<Operacion> findByActivoIdAndTipoOperacion(Long activoId, TipoOperacion tipoOperacion);

    // Traer todas las operaciones con el activo cargado (evita N+1)
    @Query("SELECT o FROM Operacion o JOIN FETCH o.activo ORDER BY o.fecha DESC")
    List<Operacion> findAllConActivo();

    @Query("SELECT o FROM Operacion o JOIN FETCH o.activo WHERE o.activo.id = :activoId ORDER BY o.fecha DESC")
    List<Operacion> findByActivoIdConActivo(@Param("activoId") Long activoId);
}
