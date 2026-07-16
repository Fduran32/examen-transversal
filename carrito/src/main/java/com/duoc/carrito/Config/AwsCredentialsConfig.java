package com.duoc.carrito.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

/**
 * Spring Cloud AWS (spring.cloud.aws.credentials.access-key / secret-key) solo soporta
 * credenciales PERMANENTES: no existe una propiedad para el session token.
 * Como AWS Academy entrega credenciales temporales (STS), es OBLIGATORIO enviar el
 * session token junto con el access key y el secret key, o AWS rechaza la petición
 * con "The security token included in the request is invalid".
 *
 * Este bean reemplaza al AwsCredentialsProvider auto-configurado (incompleto) por uno
 * que sí arma credenciales de sesión completas (access key + secret key + session token).
 */
@Configuration
public class AwsCredentialsConfig {

    @Value("${AWS_ACCESS_KEY_ID}")
    private String accessKey;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretKey;

    @Value("${AWS_SESSION_TOKEN}")
    private String sessionToken;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        AwsSessionCredentials sessionCredentials = AwsSessionCredentials.create(
                accessKey,
                secretKey,
                sessionToken
        );
        return StaticCredentialsProvider.create(sessionCredentials);
    }
}
