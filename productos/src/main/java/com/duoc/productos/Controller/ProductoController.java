package com.duoc.productos.Controller;

import com.duoc.productos.Model.ProductoModel;
import com.duoc.productos.Service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos") // Es una buena práctica darle un prefijo explícito
public class ProductoController {

    @Autowired
    private ProductoService service;

    @PostMapping
    public ResponseEntity<ProductoModel> crearProducto(@Valid @RequestBody ProductoModel producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(producto));
    }

    @GetMapping
    public ResponseEntity<List<ProductoModel>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoModel> obtenerPorId(@PathVariable Long id) {
        // Ahora es directo, rápido y eficiente
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    // === NUEVO ENDPOINT: Actualizar Stock ===
    // Permite actualizar el stock y disparar de forma automática la alerta SQS
    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductoModel> actualizarStock(
            @PathVariable Long id,
            @RequestParam int stock) {

        ProductoModel productoActualizado = service.actualizarStock(id, stock);
        return ResponseEntity.ok(productoActualizado);
    }
}