package com.duoc.productos.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

/**
 * Igual que en carrito: Spring Cloud AWS no soporta session token en
 * spring.cloud.aws.credentials.*, así que hay que armar el AwsCredentialsProvider
 * manualmente con credenciales de sesión completas (necesario para AWS Academy).
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
