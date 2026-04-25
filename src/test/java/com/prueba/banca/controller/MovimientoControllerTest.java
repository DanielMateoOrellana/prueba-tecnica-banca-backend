package com.prueba.banca.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.banca.config.SecurityConfig;
import com.prueba.banca.domain.TipoMovimiento;
import com.prueba.banca.dto.MovimientoRequest;
import com.prueba.banca.dto.MovimientoResponse;
import com.prueba.banca.exception.CupoDiarioExcedidoException;
import com.prueba.banca.exception.GlobalExceptionHandler;
import com.prueba.banca.exception.SaldoNoDisponibleException;
import com.prueba.banca.service.MovimientoService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MovimientoController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovimientoService movimientoService;

    @Test
    void postMovimiento_retiroConSaldoCero_retorna400ConMensajeSaldoNoDisponible() throws Exception {
        MovimientoRequest request = new MovimientoRequest("495878", TipoMovimiento.RETIRO, new BigDecimal("100.00"));
        when(movimientoService.registrar(any())).thenThrow(new SaldoNoDisponibleException());

        mockMvc.perform(post("/movimientos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Saldo no disponible")));
    }

    @Test
    void postMovimiento_cupoDiarioExcedido_retorna400ConMensaje() throws Exception {
        MovimientoRequest request = new MovimientoRequest("478758", TipoMovimiento.RETIRO, new BigDecimal("1100.00"));
        when(movimientoService.registrar(any())).thenThrow(new CupoDiarioExcedidoException());

        mockMvc.perform(post("/movimientos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Cupo diario Excedido")));
    }

    @Test
    void postMovimiento_depositoValido_retorna201() throws Exception {
        MovimientoRequest request = new MovimientoRequest("495878", TipoMovimiento.DEPOSITO, new BigDecimal("150.00"));
        MovimientoResponse response = new MovimientoResponse(
                1L,
                LocalDateTime.now(),
                TipoMovimiento.DEPOSITO,
                new BigDecimal("150.00"),
                new BigDecimal("150.00"),
                "495878"
        );
        when(movimientoService.registrar(any())).thenReturn(response);

        mockMvc.perform(post("/movimientos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.saldo").value(150.00))
                .andExpect(jsonPath("$.numeroCuenta").value("495878"));
    }
}
