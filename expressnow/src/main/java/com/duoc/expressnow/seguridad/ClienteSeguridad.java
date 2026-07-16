package com.duoc.expressnow.seguridad;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class ClienteSeguridad {

    // Viene de application.properties / variable de entorno, NUNCA hardcodeado.
    // Debe ser idéntica a jwt.secret en productos-service y carrito-service.
    @Value("${jwt.secret}")
    private String secretKeyStr;

    public String generarToken(String username) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora de expiración
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
