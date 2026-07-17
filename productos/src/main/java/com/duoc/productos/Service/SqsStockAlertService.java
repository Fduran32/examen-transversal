package com.duoc.productos.Service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SqsStockAlertService {

    private final SqsTemplate sqsTemplate;

    // Antes estaba hardcodeada con una cuenta de AWS de ejemplo (123456789012) que no existe.
    // Ahora se inyecta el NOMBRE de la cola real desde application.properties / docker-compose,
    // igual que hace carrito-service.
    @Value("${aws.queue.name}")
    private String queueName;

    public SqsStockAlertService(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void enviarAlertaSinStock(Long productoId, String nombreProducto) {
        try {
            // Construimos un JSON simple para la alerta
            String mensajeJson = String.format(
                    "{\"evento\":\"PRODUCTO_SIN_STOCK\", \"productoId\":%d, \"nombre\":\"%s\"}",
                    productoId, nombreProducto
            );

            sqsTemplate.send(queueName, mensajeJson);
            System.out.println("⚠️ Alerta de stock crítico enviada a AWS SQS para: " + nombreProducto);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar alerta a SQS: " + e.getMessage());
        }
    }
}
