package com.duoc.productos.ControllerTest;

import com.duoc.productos.Controller.ProductoController;
import com.duoc.productos.Model.ProductoModel;
import com.duoc.productos.Service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class) // Levanta el contexto web del controlador y aplica tu SecurityConfig
public class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService; // Simulamos la capa de servicio

    // -------------------------------------------------------------------------
    // PRUEBA 1: Crear un producto con JWT Simulado
    // -------------------------------------------------------------------------
    @Test
    void debeCrearUnProductoCuandoUsuarioEstaAutenticado() throws Exception {
        // GIVEN: El objeto que enviamos y el que esperamos que retorne el servicio
        ProductoModel nuevoProducto = new ProductoModel();
        nuevoProducto.setNombre("Mouse Gamer");
        nuevoProducto.setPrecio(25.99);
        nuevoProducto.setStock(10);

        ProductoModel productoGuardado = new ProductoModel();
        productoGuardado.setId(1L);
        productoGuardado.setNombre("Mouse Gamer");
        productoGuardado.setPrecio(25.99);
        productoGuardado.setStock(10);

        when(productoService.guardar(any(ProductoModel.class))).thenReturn(productoGuardado);

        // WHEN & THEN: Usamos .with(jwt()) para simular que viene un token JWT válido
        mockMvc.perform(post("/api/productos")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoProducto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"))
                .andExpect(jsonPath("$.precio").value(25.99));
    }

    // -------------------------------------------------------------------------
    // PRUEBA 2: Listar productos con JWT Simulado
    // -------------------------------------------------------------------------
    @Test
    void debeListarProductosCuandoUsuarioEstaAutenticado() throws Exception {
        // GIVEN
        ProductoModel p1 = new ProductoModel();
        p1.setId(1L); p1.setNombre("Teclado"); p1.setPrecio(45.0); p1.setStock(5);

        List<ProductoModel> lista = Arrays.asList(p1);
        when(productoService.listar()).thenReturn(lista);

        // WHEN & THEN
        mockMvc.perform(get("/api/productos")
                        .with(jwt())) // Simula token JWT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Teclado"));
    }

    // -------------------------------------------------------------------------
    // PRUEBA 3: Seguridad - Validar que rebote sin Token
    // -------------------------------------------------------------------------
    @Test
    void debeRetornar401AnominoCuandoNoSeEnviaToken() throws Exception {
        // WHEN & THEN: Intentamos hacer un GET directo sin usar .with(jwt())
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized()); // Debe dar HTTP 401 por tu regla .anyRequest().authenticated()
    }
}