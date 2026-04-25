package com.prueba.banca.service;

import com.prueba.banca.domain.Cuenta;
import com.prueba.banca.domain.Movimiento;
import com.prueba.banca.domain.TipoMovimiento;
import com.prueba.banca.dto.MovimientoRequest;
import com.prueba.banca.dto.MovimientoResponse;
import com.prueba.banca.exception.CupoDiarioExcedidoException;
import com.prueba.banca.exception.EntidadNotFoundException;
import com.prueba.banca.exception.SaldoNoDisponibleException;
import com.prueba.banca.exception.ValidacionException;
import com.prueba.banca.mapper.MovimientoMapper;
import com.prueba.banca.repository.CuentaRepository;
import com.prueba.banca.repository.MovimientoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoMapper movimientoMapper;
    private final BigDecimal cupoDiarioRetiro;

    public MovimientoService(MovimientoRepository movimientoRepository,
                             CuentaRepository cuentaRepository,
                             MovimientoMapper movimientoMapper,
                             @Value("${banca.cupo-diario-retiro:1000.00}") BigDecimal cupoDiarioRetiro) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
        this.movimientoMapper = movimientoMapper;
        this.cupoDiarioRetiro = cupoDiarioRetiro;
    }

    public MovimientoResponse registrar(MovimientoRequest request) {
        Cuenta cuenta = cuentaRepository.findById(request.numeroCuenta())
                .orElseThrow(() -> new EntidadNotFoundException("Cuenta no encontrada: " + request.numeroCuenta()));

        if (Boolean.FALSE.equals(cuenta.getEstado())) {
            throw new ValidacionException("La cuenta esta inactiva");
        }

        BigDecimal valorAbsoluto = request.valor().abs();
        BigDecimal valorConSigno = request.tipoMovimiento() == TipoMovimiento.RETIRO
                ? valorAbsoluto.negate()
                : valorAbsoluto;

        if (request.tipoMovimiento() == TipoMovimiento.RETIRO) {
            if (cuenta.getSaldoActual().compareTo(BigDecimal.ZERO) <= 0) {
                throw new SaldoNoDisponibleException();
            }
            BigDecimal saldoResultante = cuenta.getSaldoActual().add(valorConSigno);
            if (saldoResultante.compareTo(BigDecimal.ZERO) < 0) {
                throw new SaldoNoDisponibleException();
            }
            BigDecimal totalRetirosHoy = totalRetirosDelDia(cuenta.getNumeroCuenta(), LocalDate.now())
                    .add(valorAbsoluto);
            if (totalRetirosHoy.compareTo(cupoDiarioRetiro) > 0) {
                throw new CupoDiarioExcedidoException();
            }
        }

        cuenta.setSaldoActual(cuenta.getSaldoActual().add(valorConSigno));
        cuentaRepository.save(cuenta);

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(request.tipoMovimiento());
        movimiento.setValor(valorConSigno);
        movimiento.setSaldo(cuenta.getSaldoActual());
        movimiento.setCuenta(cuenta);

        return movimientoMapper.toResponse(movimientoRepository.save(movimiento));
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listar() {
        return movimientoRepository.findAll().stream()
                .map(movimientoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarPorCuenta(String numeroCuenta) {
        if (!cuentaRepository.existsById(numeroCuenta)) {
            throw new EntidadNotFoundException("Cuenta no encontrada: " + numeroCuenta);
        }
        return movimientoRepository.findByCuenta_NumeroCuentaOrderByFechaDesc(numeroCuenta).stream()
                .map(movimientoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimientoResponse obtener(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("Movimiento no encontrado: " + id));
        return movimientoMapper.toResponse(movimiento);
    }

    public void eliminar(Long id) {
        if (!movimientoRepository.existsById(id)) {
            throw new EntidadNotFoundException("Movimiento no encontrado: " + id);
        }
        movimientoRepository.deleteById(id);
    }

    private BigDecimal totalRetirosDelDia(String numeroCuenta, LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        BigDecimal suma = movimientoRepository.sumValorByCuentaAndTipoAndFechaBetween(
                numeroCuenta, TipoMovimiento.RETIRO, inicio, fin);
        return suma == null ? BigDecimal.ZERO : suma.abs();
    }
}
