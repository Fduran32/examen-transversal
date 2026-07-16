package com.duoc.carrito.Dto;

import lombok.Data;

@Data
public class ProductoDto {

    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock; // <-- MODIFICADO: Ahora empieza con minúscula
}