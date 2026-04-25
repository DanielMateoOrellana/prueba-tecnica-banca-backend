package com.prueba.banca.service;

import com.prueba.banca.domain.Cliente;
import com.prueba.banca.domain.Movimiento;
import com.prueba.banca.domain.TipoMovimiento;
import com.prueba.banca.dto.ReporteEntry;
import com.prueba.banca.dto.ReporteResponse;
import com.prueba.banca.exception.EntidadNotFoundException;
import com.prueba.banca.exception.ValidacionException;
import com.prueba.banca.repository.ClienteRepository;
import com.prueba.banca.repository.MovimientoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final ClienteRepository clienteRepository;
    private final MovimientoRepository movimientoRepository;

    public ReporteService(ClienteRepository clienteRepository, MovimientoRepository movimientoRepository) {
        this.clienteRepository = clienteRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public ReporteResponse generar(String clienteId, LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new ValidacionException("Las fechas 'desde' y 'hasta' son obligatorias");
        }
        if (hasta.isBefore(desde)) {
            throw new ValidacionException("La fecha 'hasta' no puede ser anterior a 'desde'");
        }

        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new EntidadNotFoundException("Cliente no encontrado: " + clienteId));

        LocalDateTime desdeDt = desde.atStartOfDay();
        LocalDateTime hastaDt = hasta.plusDays(1).atStartOfDay();

        List<Movimiento> movimientos = movimientoRepository.findByClienteIdAndFechaBetween(
                clienteId, desdeDt, hastaDt);

        List<ReporteEntry> entries = movimientos.stream()
                .map(m -> new ReporteEntry(
                        m.getFecha(),
                        cliente.getNombre(),
                        m.getCuenta().getNumeroCuenta(),
                        m.getCuenta().getTipoCuenta(),
                        m.getCuenta().getSaldoInicial(),
                        m.getCuenta().getEstado(),
                        m.getValor(),
                        m.getTipoMovimiento(),
                        m.getSaldo()
                ))
                .toList();

        BigDecimal totalDebitos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == TipoMovimiento.RETIRO)
                .map(m -> m.getValor().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == TipoMovimiento.DEPOSITO)
                .map(m -> m.getValor().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReporteResponse(
                clienteId,
                cliente.getNombre(),
                desde,
                hasta,
                entries,
                totalDebitos,
                totalCreditos
        );
    }
}
