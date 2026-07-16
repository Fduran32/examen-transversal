package com.duoc.carrito.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "carrito_items")
@Data
public class CarritoItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guardamos el ID del producto que consultamos desde el microservicio de Productos
    @Column(nullable = false)
    private Long productoId;

    private String nombreProducto;
    private Double precioUnitario;
    private Integer cantidad;

    // Relación ManyToOne: Muchos ítems pertenecen a un solo carrito
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    @JsonIgnore // Evita bucles infinitos al serializar a JSON
    @ToString.Exclude // Evita errores de recursión en el método toString de Lombok
    private CarritoModel carrito;
}