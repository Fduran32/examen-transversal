package com.duoc.carrito.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Data
public class CarritoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifica al dueño del carrito (Usuario de ExpressNow)
    @Column(nullable = false, unique = true)
    private Long usuarioId;

    // Relación OneToMany: Un carrito contiene muchos ítems
    // 'cascade = CascadeType.ALL' asegura que si se borra o actualiza el carrito, se haga lo mismo con sus ítems
    // 'orphanRemoval = true' borra automáticamente los ítems que saquemos de la lista
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CarritoItemModel> items = new ArrayList<>();

    // Campo calculado para facilitar las cosas en tu base de datos y reportes
    private Double totalCarrito;

    // Método de ayuda para recalcular el total de forma automática en tu lógica de negocio
    public void actualizarTotal() {
        this.totalCarrito = items.stream()
                .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                .sum();
    }
}