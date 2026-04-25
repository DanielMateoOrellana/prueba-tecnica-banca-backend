package com.prueba.banca.repository;

import com.prueba.banca.domain.Movimiento;
import com.prueba.banca.domain.TipoMovimiento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuenta_NumeroCuentaOrderByFechaDesc(String numeroCuenta);

    List<Movimiento> findByCuenta_NumeroCuentaAndFechaBetweenOrderByFechaAsc(
            String numeroCuenta, LocalDateTime desde, LocalDateTime hasta);

    @Query("""
            SELECT COALESCE(SUM(m.valor), 0)
            FROM Movimiento m
            WHERE m.cuenta.numeroCuenta = :numeroCuenta
              AND m.tipoMovimiento = :tipo
              AND m.fecha >= :desde
              AND m.fecha < :hasta
            """)
    BigDecimal sumValorByCuentaAndTipoAndFechaBetween(
            @Param("numeroCuenta") String numeroCuenta,
            @Param("tipo") TipoMovimiento tipo,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);

    @Query("""
            SELECT m FROM Movimiento m
            WHERE m.cuenta.cliente.clienteId = :clienteId
              AND m.fecha BETWEEN :desde AND :hasta
            ORDER BY m.fecha ASC
            """)
    List<Movimiento> findByClienteIdAndFechaBetween(
            @Param("clienteId") String clienteId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}
