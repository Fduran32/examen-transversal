package com.duoc.expressnow.repository;


import com.duoc.expressnow.model.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteModel,Long> {
    ClienteModel findByUsername(String username);
    boolean existsByUsername(String username);
}
