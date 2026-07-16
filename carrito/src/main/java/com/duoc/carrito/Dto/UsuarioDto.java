package com.duoc.carrito.Dto;



import lombok.Data;

@Data
public class UsuarioDto {
    private Long id;
    private String username;
    private String email;
    private Boolean activo; // O los campos que tengas en tu modelo de Usuarios
}