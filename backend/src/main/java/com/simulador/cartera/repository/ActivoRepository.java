package com.simulador.cartera.repository;

import com.simulador.cartera.entity.Activo;
import com.simulador.cartera.enums.TipoActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivoRepository extends JpaRepository<Activo, Long> {

    Optional<Activo> findByTickerIgnoreCase(String ticker);

    boolean existsByTickerIgnoreCase(String ticker);

    // Buscar por nombre o ticker (para el buscador del frontend)
    @Query("SELECT a FROM Activo a WHERE " +
           "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(a.ticker) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Activo> buscarPorNombreOTicker(@Param("busqueda") String busqueda);

    List<Activo> findByTipo(TipoActivo tipo);
}
