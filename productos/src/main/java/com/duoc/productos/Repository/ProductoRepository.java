package com.duoc.productos.Repository;

import com.duoc.productos.Model.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository <ProductoModel, Long> {
}
