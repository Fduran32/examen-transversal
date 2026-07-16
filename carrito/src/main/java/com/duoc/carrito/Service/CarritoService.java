package com.duoc.carrito.Service;

import com.duoc.carrito.Dto.ProductoDto;
import com.duoc.carrito.Dto.UsuarioDto;
import com.duoc.carrito.Exception.CarritoVacioException;
import com.duoc.carrito.Exception.RecursoNoEncontradoException;
import com.duoc.carrito.Exception.StockInsuficienteException;
import com.duoc.carrito.Model.CarritoItemModel;
import com.duoc.carrito.Model.CarritoModel;
import com.duoc.carrito.Repository.CarritoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository repository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private SqsProducerService sqsProducerService; // Para enviar a AWS SQS

    @Autowired
    private ObjectMapper objectMapper; // Para convertir el Carrito a JSON

    // URLs inyectadas desde application.properties / variables de entorno del docker-compose
    // (antes estaban hardcodeadas como "host.docker.internal", que no resuelve fuera de Docker Desktop)
    @Value("${usuarios.service.url}")
    private String usuariosServiceUrl;

    @Value("${productos.service.url}")
    private String productosServiceUrl;

    @Transactional
    public CarritoModel agregarAlCarrito(Long usuarioId, Long productoId, Integer cantidad, String token) {

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }

        // 1. CONEXIÓN AL MICROSERVICIO DE USUARIOS (ExpressNow - 8081)
        UsuarioDto usuario = webClientBuilder.build()
                .get()
                .uri(usuariosServiceUrl + "/api/usuarios/" + usuarioId)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(UsuarioDto.class)
                .block();

        if (usuario == null) {
            throw new RecursoNoEncontradoException("El usuario con ID " + usuarioId + " no existe.");
        }

        // 2. CONEXIÓN AL MICROSERVICIO DE PRODUCTOS (Productos - 8082)
        ProductoDto producto = webClientBuilder.build()
                .get()
                .uri(productosServiceUrl + "/api/productos/" + productoId)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(ProductoDto.class)
                .block();

        if (producto == null) {
            throw new RecursoNoEncontradoException("El producto con ID " + productoId + " no existe.");
        }

        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        // 3. RECUPERAR O CREAR EL CARRITO DEL USUARIO (Relacional - IE8)
        CarritoModel carrito = repository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    CarritoModel nuevoCarrito = new CarritoModel();
                    nuevoCarrito.setUsuarioId(usuarioId);
                    nuevoCarrito.setTotalCarrito(0.0);
                    return nuevoCarrito;
                });

        // 4. VERIFICAR SI EL PRODUCTO YA ESTABA EN EL CARRITO
        Optional<CarritoItemModel> itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst();

        if (itemExistente.isPresent()) {
            // Si ya existe, sumamos la cantidad
            CarritoItemModel item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
        } else {
            // Si es nuevo, creamos el CarritoItemModel relacional
            CarritoItemModel nuevoItem = new CarritoItemModel();
            nuevoItem.setProductoId(producto.getId());
            nuevoItem.setNombreProducto(producto.getNombre());
            nuevoItem.setPrecioUnitario(producto.getPrecio());
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setCarrito(carrito); // Establece la clave foránea relacional

            carrito.getItems().add(nuevoItem);
        }

        // 5. RECALCULAR TOTAL Y GUARDAR EN CASCADA
        carrito.actualizarTotal();
        return repository.save(carrito);
    }

    /**
     * Procesa la compra del Carrito y la envía de forma Serverless a la Cola de AWS SQS (IE18)
     */
    @Transactional
    public void realizarCheckout(Long usuarioId) {
        // Buscamos el carrito activo del usuario
        CarritoModel carrito = repository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no tiene un carrito activo."));

        if (carrito.getItems().isEmpty()) {
            throw new CarritoVacioException("El carrito está vacío, no se puede realizar la compra.");
        }

        try {
            // 1. Serializamos el objeto relacional a una cadena JSON limpia
            String carritoJson = objectMapper.writeValueAsString(carrito);

            // 2. Despachamos el JSON a la cola de AWS SQS
            sqsProducerService.enviarMensajeOrden(carritoJson);

            // 3. Vaciamos el carrito en la base de datos MySQL local (limpiando los ítems)
            repository.delete(carrito);

            System.out.println("Checkout procesado. Carrito eliminado y enviado a AWS SQS.");

        } catch (Exception e) {
            throw new RuntimeException("Error procesando el checkout en AWS: " + e.getMessage(), e);
        }
    }
}