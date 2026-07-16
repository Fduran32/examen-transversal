package com.duoc.expressnow.service;


import com.duoc.expressnow.exception.UsuarioYaExisteException;
import com.duoc.expressnow.model.ClienteModel;
import com.duoc.expressnow.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ClienteModel registrar(ClienteModel cliente){
        if (clienteRepository.existsByUsername(cliente.getUsername())) {
            throw new UsuarioYaExisteException("El username '" + cliente.getUsername() + "' ya está registrado.");
        }
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        return clienteRepository.save(cliente);
    }
}
