package org.digitalbooks;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(
        name = "jwt_token_security",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER)
public class UserServiceApplication {
    public static void main(String[] args) {
        System.out.println("Use this URL for accessing swaggerUI: http://localhost:8080/swagger-ui/index.html#/");
        SpringApplication.run(UserServiceApplication.class, args);
    }
}