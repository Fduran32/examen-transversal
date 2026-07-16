package com.duoc.carrito.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.awspring.cloud.sqs.operations.SqsTemplate;

@Service
public class SqsProducerService {

    @Autowired
    private SqsTemplate sqsTemplate;

    @Value("${aws.queue.name}")
    private String queueName;

    public void enviarMensajeOrden(String jsonMessage) {
        // Envia el mensaje JSON a la cola de SQS de forma asíncrona
        sqsTemplate.send(queueName, jsonMessage);
        System.out.println("Mensaje enviado exitosamente a AWS SQS: " + jsonMessage);
    }
}