package com.duoc.carrito.Controller;

import com.duoc.carrito.Model.CarritoModel;
import com.duoc.carrito.Service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService service;

    /**
     * Endpoint para añadir o actualizar productos en el carrito (Estructura Relacional JPA - IE8)
     */
    @PostMapping("/agregar")
    public ResponseEntity<CarritoModel> agregar(
            @RequestParam Long usuarioId,
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            @RequestHeader("Authorization") String token) { // Capturamos el token JWT para la seguridad

        CarritoModel carritoActualizado = service.agregarAlCarrito(usuarioId, productoId, cantidad, token);
        return ResponseEntity.ok(carritoActualizado);
    }

    /**
     * Endpoint de Checkout: Envía la orden del carrito a la cola AWS SQS (FaaS/Mensajería - IE18)
     * Al finalizar con éxito, el carrito local se limpia de la base de datos.
     */
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(
            @RequestParam Long usuarioId,
            @RequestHeader("Authorization") String token) { // JWT requerido por seguridad

        service.realizarCheckout(usuarioId);
        return ResponseEntity.ok("Compra procesada con éxito. La orden de compra ha sido enviada a la cola de AWS SQS para su procesamiento serverless.");
    }
}