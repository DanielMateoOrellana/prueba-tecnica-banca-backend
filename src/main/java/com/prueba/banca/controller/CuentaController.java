package com.prueba.banca.controller;

import com.prueba.banca.dto.CuentaRequest;
import com.prueba.banca.dto.CuentaResponse;
import com.prueba.banca.service.CuentaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> listar(@RequestParam(required = false) String clienteId) {
        if (clienteId != null && !clienteId.isBlank()) {
            return ResponseEntity.ok(cuentaService.listarPorCliente(clienteId));
        }
        return ResponseEntity.ok(cuentaService.listar());
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> obtener(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(cuentaService.obtener(numeroCuenta));
    }

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaRequest request) {
        CuentaResponse created = cuentaService.crear(request);
        return ResponseEntity.created(URI.create("/cuentas/" + created.numeroCuenta())).body(created);
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> actualizar(@PathVariable String numeroCuenta,
                                                     @Valid @RequestBody CuentaRequest request) {
        return ResponseEntity.ok(cuentaService.actualizar(numeroCuenta, request));
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<Void> eliminar(@PathVariable String numeroCuenta) {
        cuentaService.eliminar(numeroCuenta);
        return ResponseEntity.noContent().build();
    }
}
