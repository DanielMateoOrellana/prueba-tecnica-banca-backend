package com.prueba.banca.service;

import com.prueba.banca.domain.Cliente;
import com.prueba.banca.dto.ClienteRequest;
import com.prueba.banca.dto.ClienteResponse;
import com.prueba.banca.exception.EntidadNotFoundException;
import com.prueba.banca.exception.ValidacionException;
import com.prueba.banca.mapper.ClienteMapper;
import com.prueba.banca.repository.ClienteRepository;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtener(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("Cliente no encontrado: " + id));
        return clienteMapper.toResponse(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorClienteId(String clienteId) {
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new EntidadNotFoundException("Cliente no encontrado: " + clienteId));
        return clienteMapper.toResponse(cliente);
    }

    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.existsByClienteId(request.clienteId())) {
            throw new ValidacionException("Ya existe un cliente con clienteId: " + request.clienteId());
        }
        if (clienteRepository.existsByIdentificacion(request.identificacion())) {
            throw new ValidacionException("Ya existe un cliente con identificacion: " + request.identificacion());
        }
        Cliente cliente = clienteMapper.toEntity(request);
        cliente.setContrasena(passwordEncoder.encode(request.contrasena()));
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("Cliente no encontrado: " + id));
        clienteMapper.updateEntity(request, cliente);
        if (request.contrasena() != null && !request.contrasena().isBlank()) {
            cliente.setContrasena(passwordEncoder.encode(request.contrasena()));
        }
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntidadNotFoundException("Cliente no encontrado: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
