package com.duoc.productos.Service;

import com.duoc.productos.Model.ProductoModel;
import com.duoc.productos.Repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final SqsStockAlertService sqsStockAlertService;

    // Constructor que inyecta ambos servicios necesarios
    public ProductoService(ProductoRepository productoRepository, SqsStockAlertService sqsStockAlertService) {
        this.productoRepository = productoRepository;
        this.sqsStockAlertService = sqsStockAlertService;
    }

    // 1. MÉTODO RESTAURADO: Listar todos los productos
    public List<ProductoModel> listar() {
        return productoRepository.findAll();
    }

    // 2. MÉTODO RESTAURADO: Obtener un producto por ID
    public ProductoModel obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    // 3. MÉTODO RESTAURADO: Guardar / Crear un producto
    @Transactional
    public ProductoModel guardar(ProductoModel producto) {
        return productoRepository.save(producto);
    }

    // 4. NUEVO MÉTODO: Actualizar stock y enviar alerta SQS si llega a 0
    @Transactional
    public ProductoModel actualizarStock(Long id, int nuevoStock) {
        ProductoModel producto = obtenerPorId(id);

        producto.setStock(nuevoStock);
        ProductoModel productoActualizado = productoRepository.save(producto);

        // Si se queda sin stock, disparamos la alerta asíncrona de SQS
        if (productoActualizado.getStock() == 0) {
            sqsStockAlertService.enviarAlertaSinStock(
                    productoActualizado.getId(),
                    productoActualizado.getNombre()
            );
        }

        return productoActualizado;
    }
}