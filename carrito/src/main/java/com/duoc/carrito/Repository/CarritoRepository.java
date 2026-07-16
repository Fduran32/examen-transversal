package com.duoc.carrito.Repository;

import com.duoc.carrito.Model.CarritoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<CarritoModel, Long> {
    // Método clave para recuperar el carrito activo del cliente logueado
    Optional<CarritoModel> findByUsuarioId(Long usuarioId);
}