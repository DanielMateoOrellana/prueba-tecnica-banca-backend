package com.prueba.banca.service;

import com.prueba.banca.domain.Cliente;
import com.prueba.banca.domain.Cuenta;
import com.prueba.banca.dto.CuentaRequest;
import com.prueba.banca.dto.CuentaResponse;
import com.prueba.banca.exception.EntidadNotFoundException;
import com.prueba.banca.exception.ValidacionException;
import com.prueba.banca.mapper.CuentaMapper;
import com.prueba.banca.repository.ClienteRepository;
import com.prueba.banca.repository.CuentaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final CuentaMapper cuentaMapper;

    public CuentaService(CuentaRepository cuentaRepository,
                         ClienteRepository clienteRepository,
                         CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
        this.cuentaMapper = cuentaMapper;
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listar() {
        return cuentaRepository.findAll().stream()
                .map(cuentaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CuentaResponse obtener(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new EntidadNotFoundException("Cuenta no encontrada: " + numeroCuenta));
        return cuentaMapper.toResponse(cuenta);
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listarPorCliente(String clienteId) {
        return cuentaRepository.findByCliente_ClienteId(clienteId).stream()
                .map(cuentaMapper::toResponse)
                .toList();
    }

    public CuentaResponse crear(CuentaRequest request) {
        if (cuentaRepository.existsById(request.numeroCuenta())) {
            throw new ValidacionException("Ya existe una cuenta con numero: " + request.numeroCuenta());
        }
        Cliente cliente = clienteRepository.findByClienteId(request.clienteId())
                .orElseThrow(() -> new EntidadNotFoundException("Cliente no encontrado: " + request.clienteId()));

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(request.numeroCuenta());
        cuenta.setTipoCuenta(request.tipoCuenta());
        cuenta.setSaldoInicial(request.saldoInicial());
        cuenta.setSaldoActual(request.saldoInicial());
        cuenta.setEstado(request.estado());
        cuenta.setCliente(cliente);
        return cuentaMapper.toResponse(cuentaRepository.save(cuenta));
    }

    public CuentaResponse actualizar(String numeroCuenta, CuentaRequest request) {
        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new EntidadNotFoundException("Cuenta no encontrada: " + numeroCuenta));

        cuenta.setTipoCuenta(request.tipoCuenta());
        cuenta.setEstado(request.estado());

        if (request.clienteId() != null && !request.clienteId().equals(cuenta.getCliente().getClienteId())) {
            Cliente cliente = clienteRepository.findByClienteId(request.clienteId())
                    .orElseThrow(() -> new EntidadNotFoundException("Cliente no encontrado: " + request.clienteId()));
            cuenta.setCliente(cliente);
        }
        return cuentaMapper.toResponse(cuentaRepository.save(cuenta));
    }

    public void eliminar(String numeroCuenta) {
        if (!cuentaRepository.existsById(numeroCuenta)) {
            throw new EntidadNotFoundException("Cuenta no encontrada: " + numeroCuenta);
        }
        cuentaRepository.deleteById(numeroCuenta);
    }
}
