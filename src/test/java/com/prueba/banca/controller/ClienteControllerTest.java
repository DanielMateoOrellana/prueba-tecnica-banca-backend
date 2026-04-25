package com.prueba.banca.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.banca.config.SecurityConfig;
import com.prueba.banca.domain.Genero;
import com.prueba.banca.dto.ClienteRequest;
import com.prueba.banca.dto.ClienteResponse;
import com.prueba.banca.exception.EntidadNotFoundException;
import com.prueba.banca.exception.GlobalExceptionHandler;
import com.prueba.banca.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ClienteController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;

    @Test
    void postCliente_datosValidos_retorna201ConId() throws Exception {
        ClienteRequest request = new ClienteRequest(
                "Jose Lema", Genero.MASCULINO, 35, "0102030405",
                "Otavalo sn y principal", "098254785", "jlema", "1234", true);
        ClienteResponse response = new ClienteResponse(
                10L, "Jose Lema", Genero.MASCULINO, 35, "0102030405",
                "Otavalo sn y principal", "098254785", "jlema", true);

        when(clienteService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.clienteId").value("jlema"));
    }

    @Test
    void getCliente_inexistente_retorna404() throws Exception {
        when(clienteService.obtener(anyLong()))
                .thenThrow(new EntidadNotFoundException("Cliente no encontrado: 999"));

        mockMvc.perform(get("/clientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente no encontrado: 999"));
    }

    @Test
    void postCliente_sinNombre_retorna400() throws Exception {
        ClienteRequest invalido = new ClienteRequest(
                "", Genero.MASCULINO, 35, "0102030405",
                "Otavalo", "098254785", "jlema", "1234", true);

        mockMvc.perform(post("/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }
}
