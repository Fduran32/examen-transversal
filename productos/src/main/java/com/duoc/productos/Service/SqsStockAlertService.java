package com.duoc.productos.Service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.stereotype.Service;

@Service
public class SqsStockAlertService {

    private final SqsTemplate sqsTemplate;

    // URL de la cola SQS de alertas en AWS
    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/expressnow-alertas-stock-cola";

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

            sqsTemplate.send(to -> to
                    .queue(queueUrl)
                    .payload(mensajeJson)
            );
            System.out.println("⚠️ Alerta de stock crítico enviada a AWS SQS para: " + nombreProducto);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar alerta a SQS: " + e.getMessage());
        }
    }
}