package com.prueba.banca.controller;

import com.prueba.banca.dto.MovimientoRequest;
import com.prueba.banca.dto.MovimientoResponse;
import com.prueba.banca.service.MovimientoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listar(@RequestParam(required = false) String numeroCuenta) {
        if (numeroCuenta != null && !numeroCuenta.isBlank()) {
            return ResponseEntity.ok(movimientoService.listarPorCuenta(numeroCuenta));
        }
        return ResponseEntity.ok(movimientoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<MovimientoResponse> registrar(@Valid @RequestBody MovimientoRequest request) {
        MovimientoResponse created = movimientoService.registrar(request);
        return ResponseEntity.created(URI.create("/movimientos/" + created.id())).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
